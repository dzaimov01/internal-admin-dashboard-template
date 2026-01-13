package com.example.admin.infra;

import com.example.admin.domain.Customer;
import com.example.admin.domain.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
  Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
  Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
