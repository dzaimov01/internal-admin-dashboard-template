package com.example.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String actorEmail;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  private String entityType;

  @Column(nullable = false)
  private String entityId;

  @Column(length = 4000)
  private String changeSummary;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private String ipAddress;

  @Column(nullable = false)
  private String userAgent;

  protected AuditLog() {}

  public AuditLog(
      String actorEmail,
      String action,
      String entityType,
      String entityId,
      String changeSummary,
      OffsetDateTime createdAt,
      String ipAddress,
      String userAgent) {
    this.actorEmail = actorEmail;
    this.action = action;
    this.entityType = entityType;
    this.entityId = entityId;
    this.changeSummary = changeSummary;
    this.createdAt = createdAt;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
  }

  public Long getId() {
    return id;
  }

  public String getActorEmail() {
    return actorEmail;
  }

  public String getAction() {
    return action;
  }

  public String getEntityType() {
    return entityType;
  }

  public String getEntityId() {
    return entityId;
  }

  public String getChangeSummary() {
    return changeSummary;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }
}
