package com.museogestion.backend.repository;

import com.museogestion.backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByReservaUsuarioIdOrderByEmitidoEnDesc(Long usuarioId);

    List<Ticket> findAllByOrderByEmitidoEnDesc();

    Optional<Ticket> findByReservaId(Long reservaId);

    boolean existsByCodigo(String codigo);
}