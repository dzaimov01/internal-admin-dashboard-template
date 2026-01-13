package com.example.admin.app;

import com.example.admin.domain.Ticket;
import com.example.admin.domain.TicketPriority;
import com.example.admin.domain.TicketStatus;
import com.example.admin.infra.TicketRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {
  private final TicketRepository ticketRepository;

  public TicketService(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public Page<Ticket> list(TicketStatus status, TicketPriority priority, Pageable pageable) {
    if (status != null) {
      return ticketRepository.findByStatus(status, pageable);
    }
    if (priority != null) {
      return ticketRepository.findByPriority(priority, pageable);
    }
    return ticketRepository.findAll(pageable);
  }

  public Ticket get(Long id) {
    return ticketRepository.findById(id).orElseThrow();
  }

  public Ticket create(Ticket ticket) {
    return ticketRepository.save(ticket);
  }

  @Transactional
  public Ticket update(Long id, Ticket ticket) {
    Ticket existing = get(id);
    existing.update(ticket.getTitle(), ticket.getPriority(), ticket.getStatus(), ticket.getAssignedTo());
    return existing;
  }

  public void delete(Long id) {
    ticketRepository.deleteById(id);
  }

  @Transactional
  public int bulkUpdateStatus(List<Long> ids, TicketStatus status) {
    int updated = 0;
    for (Long id : ids) {
      Ticket ticket = get(id);
      ticket.update(ticket.getTitle(), ticket.getPriority(), status, ticket.getAssignedTo());
      updated++;
    }
    return updated;
  }
}
