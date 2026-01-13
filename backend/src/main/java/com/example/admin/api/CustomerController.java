package com.example.admin.api;

import com.example.admin.app.AuditService;
import com.example.admin.app.CustomerService;
import com.example.admin.app.SecurityContextService;
import com.example.admin.domain.Customer;
import com.example.admin.domain.CustomerStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/customers")
public class CustomerController {
  private final CustomerService customerService;
  private final AuditService auditService;
  private final SecurityContextService securityContextService;

  public CustomerController(
      CustomerService customerService,
      AuditService auditService,
      SecurityContextService securityContextService) {
    this.customerService = customerService;
    this.auditService = auditService;
    this.securityContextService = securityContextService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('customers:read')")
  public Page<Customer> list(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) CustomerStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "ASC") String direction) {
    Sort sorting = Sort.by(Sort.Direction.fromString(direction), sort);
    Pageable pageable = PageRequest.of(page, size, sorting);
    return customerService.list(search, status, pageable);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('customers:read')")
  public Customer get(@PathVariable Long id) {
    return customerService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('customers:write')")
  public Customer create(@Valid @RequestBody CustomerRequest request, HttpServletRequest servletRequest) {
    Customer created = customerService.create(
        new Customer(request.name(), request.email(), request.status(), request.notes()));
    audit("CREATE", "Customer", String.valueOf(created.getId()), "Created customer", servletRequest);
    return created;
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('customers:write')")
  public Customer update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request, HttpServletRequest servletRequest) {
    Customer updated = customerService.update(
        id,
        new Customer(request.name(), request.email(), request.status(), request.notes()));
    audit("UPDATE", "Customer", String.valueOf(id), "Updated customer", servletRequest);
    return updated;
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('customers:write')")
  public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest servletRequest) {
    customerService.delete(id);
    audit("DELETE", "Customer", String.valueOf(id), "Deleted customer", servletRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/bulk/status")
  @PreAuthorize("hasAuthority('customers:write')")
  public ResponseEntity<BulkResult> bulkUpdateStatus(@Valid @RequestBody BulkStatusRequest request, HttpServletRequest servletRequest) {
    int updated = customerService.bulkUpdateStatus(request.ids(), request.status());
    audit("BULK_UPDATE", "Customer", "bulk", "Updated status for " + updated + " customers", servletRequest);
    return ResponseEntity.ok(new BulkResult(updated));
  }

  @GetMapping("/export")
  @PreAuthorize("hasAuthority('customers:read')")
  public ResponseEntity<byte[]> exportCsv() {
    byte[] csv = customerService.exportCsv();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv")
        .contentType(MediaType.TEXT_PLAIN)
        .body(csv);
  }

  @PostMapping("/import")
  @PreAuthorize("hasAuthority('customers:write')")
  public ResponseEntity<ImportResult> importCsv(@RequestBody String csvContent, HttpServletRequest servletRequest) throws IOException {
    List<Customer> imported = customerService.importCsv(csvContent);
    audit("IMPORT", "Customer", "bulk", "Imported " + imported.size() + " customers", servletRequest);
    return ResponseEntity.ok(new ImportResult(imported.size()));
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

  public record CustomerRequest(
      @NotBlank String name,
      @Email @NotBlank String email,
      @NotNull CustomerStatus status,
      String notes) {}

  public record BulkStatusRequest(@NotNull List<Long> ids, @NotNull CustomerStatus status) {}

  public record BulkResult(int updated) {}

  public record ImportResult(int imported) {}
}
