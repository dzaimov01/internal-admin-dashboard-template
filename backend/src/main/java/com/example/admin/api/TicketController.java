package com.example.admin.api;

import com.example.admin.app.AuditService;
import com.example.admin.app.SecurityContextService;
import com.example.admin.app.TicketService;
import com.example.admin.domain.Ticket;
import com.example.admin.domain.TicketPriority;
import com.example.admin.domain.TicketStatus;
import com.example.admin.domain.User;
import com.example.admin.infra.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  private final TicketService ticketService;
  private final UserRepository userRepository;
  private final AuditService auditService;
  private final SecurityContextService securityContextService;

  public TicketController(
      TicketService ticketService,
      UserRepository userRepository,
      AuditService auditService,
      SecurityContextService securityContextService) {
    this.ticketService = ticketService;
    this.userRepository = userRepository;
    this.auditService = auditService;
    this.securityContextService = securityContextService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('tickets:read')")
  public Page<Ticket> list(
      @RequestParam(required = false) TicketStatus status,
      @RequestParam(required = false) TicketPriority priority,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "DESC") String direction) {
    Sort sorting = Sort.by(Sort.Direction.fromString(direction), sort);
    Pageable pageable = PageRequest.of(page, size, sorting);
    return ticketService.list(status, priority, pageable);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('tickets:read')")
  public Ticket get(@PathVariable Long id) {
    return ticketService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('tickets:write')")
  public Ticket create(@Valid @RequestBody TicketRequest request, HttpServletRequest servletRequest) {
    User assignee = request.assignedTo() == null ? null : userRepository.findById(request.assignedTo()).orElse(null);
    Ticket created = ticketService.create(
        new Ticket(request.title(), request.priority(), request.status(), assignee));
    audit("CREATE", "Ticket", String.valueOf(created.getId()), "Created ticket", servletRequest);
    return created;
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('tickets:write')")
  public Ticket update(@PathVariable Long id, @Valid @RequestBody TicketRequest request, HttpServletRequest servletRequest) {
    User assignee = request.assignedTo() == null ? null : userRepository.findById(request.assignedTo()).orElse(null);
    Ticket updated = ticketService.update(
        id,
        new Ticket(request.title(), request.priority(), request.status(), assignee));
    audit("UPDATE", "Ticket", String.valueOf(id), "Updated ticket", servletRequest);
    return updated;
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('tickets:write')")
  public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest servletRequest) {
    ticketService.delete(id);
    audit("DELETE", "Ticket", String.valueOf(id), "Deleted ticket", servletRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/bulk/status")
  @PreAuthorize("hasAuthority('tickets:write')")
  public ResponseEntity<BulkResult> bulkUpdateStatus(
      @Valid @RequestBody BulkStatusRequest request, HttpServletRequest servletRequest) {
    if (request.ids().isEmpty()) {
      throw new IllegalArgumentException("No ticket IDs provided");
    }
    int updated = ticketService.bulkUpdateStatus(request.ids(), request.status());
    audit("BULK_UPDATE", "Ticket", "bulk", "Updated status for " + updated + " tickets", servletRequest);
    return ResponseEntity.ok(new BulkResult(updated));
  }

  private void audit(
      String action, String entityType, String entityId, String summary, HttpServletRequest request) {
    String email = securityContextService.currentUserEmail().orElse("system");
    auditService.record(
        email,
        action,
        entityType,
        entityId,
        summary,
        request.getRemoteAddr(),
        request.getHeader("User-Agent") == null ? "" : request.getHeader("User-Agent"));
  }

  public record TicketRequest(
      @NotBlank String title,
      @NotNull TicketPriority priority,
      @NotNull TicketStatus status,
      Long assignedTo) {}

  public record BulkStatusRequest(@NotNull List<Long> ids, @NotNull TicketStatus status) {}

  public record BulkResult(int updated) {}
}
