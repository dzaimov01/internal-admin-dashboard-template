package com.example.admin.api;

import com.example.admin.app.AuditService;
import com.example.admin.app.OrderService;
import com.example.admin.app.SecurityContextService;
import com.example.admin.domain.Customer;
import com.example.admin.domain.OrderStatus;
import com.example.admin.domain.PurchaseOrder;
import com.example.admin.infra.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;
  private final CustomerRepository customerRepository;
  private final AuditService auditService;
  private final SecurityContextService securityContextService;

  public OrderController(
      OrderService orderService,
      CustomerRepository customerRepository,
      AuditService auditService,
      SecurityContextService securityContextService) {
    this.orderService = orderService;
    this.customerRepository = customerRepository;
    this.auditService = auditService;
    this.securityContextService = securityContextService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('orders:read')")
  public Page<PurchaseOrder> list(
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sort,
      @RequestParam(defaultValue = "DESC") String direction) {
    Sort sorting = Sort.by(Sort.Direction.fromString(direction), sort);
    Pageable pageable = PageRequest.of(page, size, sorting);
    return orderService.list(status, pageable);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('orders:read')")
  public PurchaseOrder get(@PathVariable Long id) {
    return orderService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('orders:write')")
  public PurchaseOrder create(@Valid @RequestBody OrderRequest request, HttpServletRequest servletRequest) {
    Customer customer = customerRepository.findById(request.customerId()).orElseThrow();
    PurchaseOrder created = orderService.create(
        new PurchaseOrder(customer, request.amount(), request.status(), OffsetDateTime.now()));
    audit("CREATE", "Order", String.valueOf(created.getId()), "Created order", servletRequest);
    return created;
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('orders:write')")
  public PurchaseOrder update(@PathVariable Long id, @Valid @RequestBody OrderRequest request, HttpServletRequest servletRequest) {
    Customer customer = customerRepository.findById(request.customerId()).orElseThrow();
    PurchaseOrder updated = orderService.update(
        id,
        new PurchaseOrder(customer, request.amount(), request.status(), OffsetDateTime.now()));
    audit("UPDATE", "Order", String.valueOf(id), "Updated order", servletRequest);
    return updated;
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('orders:write')")
  public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest servletRequest) {
    orderService.delete(id);
    audit("DELETE", "Order", String.valueOf(id), "Deleted order", servletRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/bulk/status")
  @PreAuthorize("hasAuthority('orders:write')")
  public ResponseEntity<BulkResult> bulkUpdateStatus(
      @Valid @RequestBody BulkStatusRequest request, HttpServletRequest servletRequest) {
    int updated = orderService.bulkUpdateStatus(request.ids(), request.status());
    audit("BULK_UPDATE", "Order", "bulk", "Updated status for " + updated + " orders", servletRequest);
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

  public record OrderRequest(
      @NotNull Long customerId,
      @NotNull BigDecimal amount,
      @NotNull OrderStatus status) {}

  public record BulkStatusRequest(@NotNull List<Long> ids, @NotNull OrderStatus status) {}

  public record BulkResult(int updated) {}
}
