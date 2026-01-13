package com.example.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets")
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketPriority priority;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to")
  private User assignedTo;

  protected Ticket() {}

  public Ticket(String title, TicketPriority priority, TicketStatus status, User assignedTo) {
    this.title = title;
    this.priority = priority;
    this.status = status;
    this.assignedTo = assignedTo;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public TicketPriority getPriority() {
    return priority;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public User getAssignedTo() {
    return assignedTo;
  }

  public void update(String title, TicketPriority priority, TicketStatus status, User assignedTo) {
    this.title = title;
    this.priority = priority;
    this.status = status;
    this.assignedTo = assignedTo;
  }
}
