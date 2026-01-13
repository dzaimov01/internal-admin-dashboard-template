package com.example.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_flags")
public class FeatureFlag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String key;

  @Column(nullable = false)
  private boolean enabled;

  @Column(nullable = false)
  private String description;

  protected FeatureFlag() {}

  public FeatureFlag(String key, boolean enabled, String description) {
    this.key = key;
    this.enabled = enabled;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getDescription() {
    return description;
  }

  public void update(boolean enabled) {
    this.enabled = enabled;
  }
}
