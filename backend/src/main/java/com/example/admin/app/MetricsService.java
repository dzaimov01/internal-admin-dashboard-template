package com.example.admin.app;

import com.example.admin.domain.CustomerStatus;
import com.example.admin.domain.OrderStatus;
import com.example.admin.domain.TicketPriority;
import com.example.admin.infra.CustomerRepository;
import com.example.admin.infra.OrderRepository;
import com.example.admin.infra.TicketRepository;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
  private final OrderRepository orderRepository;
  private final TicketRepository ticketRepository;
  private final CustomerRepository customerRepository;

  public MetricsService(
      OrderRepository orderRepository,
      TicketRepository ticketRepository,
      CustomerRepository customerRepository) {
    this.orderRepository = orderRepository;
    this.ticketRepository = ticketRepository;
    this.customerRepository = customerRepository;
  }

  public MetricsSnapshot snapshot() {
    Map<OrderStatus, Long> orders = new EnumMap<>(OrderStatus.class);
    for (OrderStatus status : OrderStatus.values()) {
      orders.put(status, orderRepository.countByStatus(status));
    }

    Map<TicketPriority, Long> tickets = new EnumMap<>(TicketPriority.class);
    for (TicketPriority priority : TicketPriority.values()) {
      tickets.put(priority, ticketRepository.countByPriority(priority));
    }

    long activeCustomers = customerRepository.findByStatus(CustomerStatus.ACTIVE, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

    return new MetricsSnapshot(orders, tickets, activeCustomers);
  }

  public record MetricsSnapshot(
      Map<OrderStatus, Long> ordersByStatus,
      Map<TicketPriority, Long> ticketsByPriority,
      long activeCustomers) {}
}
