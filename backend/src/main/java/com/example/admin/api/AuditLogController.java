package com.example.admin.api;

import com.example.admin.domain.AuditLog;
import com.example.admin.infra.AuditLogRepository;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {
  private final AuditLogRepository auditLogRepository;

  public AuditLogController(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('audit:read')")
  public List<AuditLog> list(
      @RequestParam(required = false) @Size(max = 120) String actorEmail,
      @RequestParam(required = false) @Size(max = 120) String action,
      @RequestParam(required = false) @Size(max = 120) String entityType) {
    Specification<AuditLog> spec = Specification.where(null);
    if (actorEmail != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("actorEmail"), actorEmail));
    }
    if (action != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("action"), action));
    }
    if (entityType != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("entityType"), entityType));
    }
    return auditLogRepository.findAll(spec);
  }
}
