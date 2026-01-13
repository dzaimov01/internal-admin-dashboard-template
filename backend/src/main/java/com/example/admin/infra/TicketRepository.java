package com.example.admin.infra;

import com.example.admin.domain.Ticket;
import com.example.admin.domain.TicketPriority;
import com.example.admin.domain.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
  Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);
  Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

  @Query("select t.priority, count(t) from Ticket t group by t.priority")
  List<Object[]> countByPriority();

  long countByPriority(TicketPriority priority);
}
