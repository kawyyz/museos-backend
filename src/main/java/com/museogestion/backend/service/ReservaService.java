package com.museogestion.backend.service;

import com.museogestion.backend.dto.ReservaDto.ReservaRequest;
import com.museogestion.backend.dto.ReservaDto.ReservaResponse;
import com.museogestion.backend.exception.ReglaNegocioException;
import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.model.*;
import com.museogestion.backend.repository.MuseoRepository;
import com.museogestion.backend.repository.ReservaRepository;
import com.museogestion.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReservaService {

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    private final ReservaRepository reservas;
    private final MuseoRepository museos;
    private final TicketRepository tickets;
    private final UsuarioService usuarios;

    public ReservaService(ReservaRepository reservas, MuseoRepository museos,
                          TicketRepository tickets, UsuarioService usuarios) {
        this.reservas = reservas;
        this.museos = museos;
        this.tickets = tickets;
        this.usuarios = usuarios;
    }

    @Transactional
    public ReservaResponse crear(String correo, ReservaRequest r) {
        Usuario usuario = usuarios.porCorreo(correo);
        Museo museo = museoBloqueado(r.museoId());
        validar(museo, r.fecha(), r.personas(), -1L);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMuseo(museo);
        reserva.setFecha(r.fecha());
        reserva.setPersonas(r.personas());
        reserva.setTotal(r.personas() * museo.getPrecioEntrada());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        return ReservaResponse.de(reservas.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> mias(String correo) {
        Usuario usuario = usuarios.porCorreo(correo);
        return reservas.findByUsuarioIdOrderByCreadaEnDesc(usuario.getId())
                .stream().map(ReservaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> todas() {
        return reservas.findAllByOrderByCreadaEnDesc().stream().map(ReservaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtener(String correo, boolean admin, Long id) {
        return ReservaResponse.de(buscar(id, correo, admin));
    }

    @Transactional
    public ReservaResponse actualizar(String correo, Long id, ReservaRequest r) {
        Reserva reserva = buscar(id, correo, false);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden modificar reservas pendientes");
        }
        Museo museo = museoBloqueado(r.museoId());
        validar(museo, r.fecha(), r.personas(), reserva.getId()); // no se cuenta a sí misma

        reserva.setMuseo(museo);
        reserva.setFecha(r.fecha());
        reserva.setPersonas(r.personas());
        reserva.setTotal(r.personas() * museo.getPrecioEntrada());
        return ReservaResponse.de(reservas.save(reserva));
    }

    @Transactional
    public ReservaResponse cancelar(String correo, boolean admin, Long id) {
        Reserva reserva = buscar(id, correo, admin);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("La reserva ya está cancelada");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        tickets.findByReservaId(reserva.getId()).ifPresent(t -> {
            t.setEstado(EstadoTicket.ANULADO);
            tickets.save(t);
        });
        return ReservaResponse.de(reservas.save(reserva));
    }

    private Museo museoBloqueado(Long museoId) {
        return museos.findByIdForUpdate(museoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el museo con id " + museoId));
    }

    private void validar(Museo museo, LocalDate fecha, int personas, Long excluirReservaId) {
        if (!museo.isActivo()) {
            throw new ReglaNegocioException("El museo no está disponible para reservas");
        }
        if (fecha.isBefore(LocalDate.now(ZONA))) {
            throw new ReglaNegocioException("La fecha de visita no puede ser anterior a hoy");
        }
        long reservadas = reservas.personasReservadas(museo.getId(), fecha, EstadoReserva.CANCELADA, excluirReservaId);
        long disponibles = Math.max(0, museo.getCapacidadDiaria() - reservadas);
        if (personas > disponibles) {
            throw new ReglaNegocioException(
                    "No hay cupos suficientes para esa fecha. Quedan " + disponibles + " lugares disponibles");
        }
    }

    
    private Reserva buscar(Long id, String correo, boolean admin) {
        Reserva reserva = reservas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva con id " + id));
        if (!admin && !reserva.getUsuario().getCorreo().equalsIgnoreCase(correo)) {
            throw new RecursoNoEncontradoException("No existe la reserva con id " + id);
        }
        return reserva;
    }
}