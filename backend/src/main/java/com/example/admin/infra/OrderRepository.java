package com.example.admin.infra;

import com.example.admin.domain.OrderStatus;
import com.example.admin.domain.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<PurchaseOrder, Long> {
  Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);

  @Query("select o.status, count(o) from PurchaseOrder o group by o.status")
  List<Object[]> countByStatus();

  long countByStatus(OrderStatus status);
}
