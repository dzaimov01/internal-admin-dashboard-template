package com.example.admin.app;

import com.example.admin.domain.Customer;
import com.example.admin.domain.CustomerStatus;
import com.example.admin.infra.CustomerRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Page<Customer> list(String search, CustomerStatus status, Pageable pageable) {
    if (status != null) {
      return customerRepository.findByStatus(status, pageable);
    }
    if (search != null && !search.isBlank()) {
      return customerRepository.findByNameContainingIgnoreCase(search, pageable);
    }
    return customerRepository.findAll(pageable);
  }

  public Customer get(Long id) {
    return customerRepository.findById(id).orElseThrow();
  }

  public Customer create(Customer customer) {
    return customerRepository.save(customer);
  }

  @Transactional
  public Customer update(Long id, Customer customer) {
    Customer existing = get(id);
    existing.update(customer.getName(), customer.getEmail(), customer.getStatus(), customer.getNotes());
    return existing;
  }

  public void delete(Long id) {
    customerRepository.deleteById(id);
  }

  @Transactional
  public int bulkUpdateStatus(List<Long> ids, CustomerStatus status) {
    int updated = 0;
    for (Long id : ids) {
      Customer customer = get(id);
      customer.update(customer.getName(), customer.getEmail(), status, customer.getNotes());
      updated++;
    }
    return updated;
  }

  public byte[] exportCsv() {
    StringBuilder sb = new StringBuilder("id,name,email,status,notes\n");
    for (Customer customer : customerRepository.findAll()) {
      sb.append(customer.getId()).append(",")
          .append(escape(customer.getName())).append(",")
          .append(escape(customer.getEmail())).append(",")
          .append(customer.getStatus()).append(",")
          .append(escape(customer.getNotes()))
          .append("\n");
    }
    return sb.toString().getBytes(StandardCharsets.UTF_8);
  }

  @Transactional
  public List<Customer> importCsv(String csvContent) throws IOException {
    List<Customer> imported = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
      String line = reader.readLine();
      if (line == null) {
        return imported;
      }
      int rowCount = 0;
      while ((line = reader.readLine()) != null) {
        if (rowCount++ > 1000) {
          break;
        }
        String[] parts = line.split(",", -1);
        if (parts.length < 4) {
          continue;
        }
        String name = parts[1].trim();
        String email = parts[2].trim();
        CustomerStatus status;
        try {
          status = CustomerStatus.valueOf(parts[3].trim());
        } catch (IllegalArgumentException ex) {
          continue;
        }
        String notes = parts.length > 4 ? parts[4].trim() : null;
        if (name.isBlank() || email.isBlank() || !email.contains("@")) {
          continue;
        }
        imported.add(customerRepository.save(new Customer(name, email, status, notes)));
      }
    }
    return imported;
  }

  private String escape(String value) {
    if (value == null) {
      return "";
    }
    String escaped = value.replace("\"", "\"\"");
    if (escaped.contains(",") || escaped.contains("\n")) {
      return '"' + escaped + '"';
    }
    return escaped;
  }
}
