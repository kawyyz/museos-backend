package com.museogestion.backend.service;

import com.museogestion.backend.dto.PagoDto.PagoRequest;
import com.museogestion.backend.dto.PagoDto.PagoResponse;
import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.exception.ReglaNegocioException;
import com.museogestion.backend.model.*;
import com.museogestion.backend.repository.PagoRepository;
import com.museogestion.backend.repository.ReservaRepository;
import com.museogestion.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class PagoService {

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    private final ReservaRepository reservas;
    private final PagoRepository pagos;
    private final TicketRepository tickets;

    public PagoService(ReservaRepository reservas, PagoRepository pagos, TicketRepository tickets) {
        this.reservas = reservas;
        this.pagos = pagos;
        this.tickets = tickets;
    }

    @Transactional
    public PagoResponse pagar(String correo, Long reservaId, PagoRequest p) {
        
        Reserva reserva = reservas.findByIdForUpdate(reservaId)
                .filter(r -> r.getUsuario().getCorreo().equalsIgnoreCase(correo))
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva con id " + reservaId));

        if (reserva.getEstado() == EstadoReserva.PAGADA) {
            throw new ReglaNegocioException("La reserva ya fue pagada");
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("No se puede pagar una reserva cancelada");
        }
        if (reserva.getFecha().isBefore(LocalDate.now(ZONA))) {
            throw new ReglaNegocioException("La fecha de la reserva ya pasó");
        }

        String digitos = p.numeroTarjeta().replaceAll("\\s", "");
        if (!digitos.matches("\\d{13,19}")) {
            throw new ReglaNegocioException("El número de tarjeta no es válido");
        }
        YearMonth vence = YearMonth.of(2000 + Integer.parseInt(p.vencimiento().substring(3)),
                Integer.parseInt(p.vencimiento().substring(0, 2)));
        if (vence.isBefore(YearMonth.now(ZONA))) {
            throw new ReglaNegocioException("La tarjeta está vencida");
        }

        
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(reserva.getTotal());
        pago.setUltimos4(digitos.substring(digitos.length() - 4));
        pago.setEstado(EstadoPago.APROBADO);
        pagos.save(pago);

        reserva.setEstado(EstadoReserva.PAGADA);
        reservas.save(reserva);

        Ticket ticket = new Ticket();
        ticket.setReserva(reserva);
        ticket.setCodigo(generarCodigo());
        ticket.setEstado(EstadoTicket.VIGENTE);
        tickets.save(ticket);

        return new PagoResponse(
                pago.getId(),
                reserva.getId(),
                pago.getMonto(),
                "**** **** **** " + pago.getUltimos4(),
                pago.getEstado().name(),
                pago.getPagadoEn(),
                ticket.getCodigo());
    }

    private String generarCodigo() {
        String codigo;
        do {
            codigo = "MG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (tickets.existsByCodigo(codigo));
        return codigo;
    }
}