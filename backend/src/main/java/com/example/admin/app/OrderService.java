package com.example.admin.app;

import com.example.admin.domain.OrderStatus;
import com.example.admin.domain.PurchaseOrder;
import com.example.admin.infra.OrderRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private final OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Page<PurchaseOrder> list(OrderStatus status, Pageable pageable) {
    if (status != null) {
      return orderRepository.findByStatus(status, pageable);
    }
    return orderRepository.findAll(pageable);
  }

  public PurchaseOrder get(Long id) {
    return orderRepository.findById(id).orElseThrow();
  }

  public PurchaseOrder create(PurchaseOrder order) {
    return orderRepository.save(order);
  }

  @Transactional
  public PurchaseOrder update(Long id, PurchaseOrder order) {
    PurchaseOrder existing = get(id);
    existing.update(order.getCustomer(), order.getAmount(), order.getStatus());
    return existing;
  }

  public void delete(Long id) {
    orderRepository.deleteById(id);
  }

  @Transactional
  public int bulkUpdateStatus(List<Long> ids, OrderStatus status) {
    int updated = 0;
    for (Long id : ids) {
      PurchaseOrder order = get(id);
      order.update(order.getCustomer(), order.getAmount(), status);
      updated++;
    }
    return updated;
  }
}
