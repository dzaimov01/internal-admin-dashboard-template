package com.example.admin.app;

import com.example.admin.domain.AuditLog;
import com.example.admin.infra.AuditLogRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
  private final AuditLogRepository auditLogRepository;

  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void record(
      String actorEmail,
      String action,
      String entityType,
      String entityId,
      String changeSummary,
      String ipAddress,
      String userAgent) {
    auditLogRepository.save(
        new AuditLog(
            actorEmail,
            action,
            entityType,
            entityId,
            changeSummary,
            OffsetDateTime.now(),
            ipAddress,
            userAgent));
  }
}
