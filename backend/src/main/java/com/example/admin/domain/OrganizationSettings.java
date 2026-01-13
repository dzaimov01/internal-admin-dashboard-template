package com.example.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "organization_settings")
public class OrganizationSettings {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String organizationName;

  @Column(nullable = false)
  private String timezone;

  @Column(nullable = false)
  private String supportEmail;

  protected OrganizationSettings() {}

  public OrganizationSettings(String organizationName, String timezone, String supportEmail) {
    this.organizationName = organizationName;
    this.timezone = timezone;
    this.supportEmail = supportEmail;
  }

  public Long getId() {
    return id;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public String getTimezone() {
    return timezone;
  }

  public String getSupportEmail() {
    return supportEmail;
  }

  public void update(String organizationName, String timezone, String supportEmail) {
    this.organizationName = organizationName;
    this.timezone = timezone;
    this.supportEmail = supportEmail;
  }
}
