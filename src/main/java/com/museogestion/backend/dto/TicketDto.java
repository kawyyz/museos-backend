package com.museogestion.backend.dto;

import com.museogestion.backend.model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketDto {

    public record TicketResponse(
            Long id,
            String codigo,
            String estado,
            Long reservaId,
            String museo,
            LocalDate fecha,
            int personas,
            int total,
            LocalDateTime emitidoEn) {

        public static TicketResponse de(Ticket t) {
            return new TicketResponse(
                    t.getId(),
                    t.getCodigo(),
                    t.getEstado().name(),
                    t.getReserva().getId(),
                    t.getReserva().getMuseo().getNombre(),
                    t.getReserva().getFecha(),
                    t.getReserva().getPersonas(),
                    t.getReserva().getTotal(),
                    t.getEmitidoEn());
        }
    }
}