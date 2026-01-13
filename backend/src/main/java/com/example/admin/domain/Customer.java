package com.example.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CustomerStatus status;

  @Column(length = 2000)
  private String notes;

  protected Customer() {}

  public Customer(String name, String email, CustomerStatus status, String notes) {
    this.name = name;
    this.email = email;
    this.status = status;
    this.notes = notes;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public CustomerStatus getStatus() {
    return status;
  }

  public String getNotes() {
    return notes;
  }

  public void update(String name, String email, CustomerStatus status, String notes) {
    this.name = name;
    this.email = email;
    this.status = status;
    this.notes = notes;
  }
}
