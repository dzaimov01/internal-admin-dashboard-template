package com.example.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, unique = true, length = 512)
  private String token;

  @Column(nullable = false)
  private OffsetDateTime expiresAt;

  protected RefreshToken() {}

  public RefreshToken(User user, String token, OffsetDateTime expiresAt) {
    this.user = user;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getToken() {
    return token;
  }

  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }
}
