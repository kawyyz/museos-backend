
package com.museogestion.backend.service;

import com.museogestion.backend.dto.ReservaDto.ReservaRequest;
import com.museogestion.backend.dto.ReservaDto.ReservaResponse;
import com.museogestion.backend.exception.ReglaNegocioException;
import com.museogestion.backend.model.EstadoReserva;
import com.museogestion.backend.model.Museo;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.model.Rol;
import com.museogestion.backend.repository.MuseoRepository;
import com.museogestion.backend.repository.ReservaRepository;
import com.museogestion.backend.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.museogestion.backend.model.Reserva;
import com.museogestion.backend.model.Ticket;
import com.museogestion.backend.model.EstadoTicket;
import com.museogestion.backend.exception.RecursoNoEncontradoException;
import java.util.List;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservas;

    @Mock
    private MuseoRepository museos;

    @Mock
    private TicketRepository tickets;

    @Mock
    private UsuarioService usuarios;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    private Museo crearMuseo() {
        Museo museo = new Museo();
        museo.setId(10L);
        museo.setNombre("Museo Historico");
        museo.setPrecioEntrada(5000);
        museo.setCapacidadDiaria(50);
        museo.setActivo(true);
        return museo;
    }

    private Reserva crearReserva() {
        Reserva reserva = new Reserva();
        reserva.setUsuario(crearUsuario());
        reserva.setMuseo(crearMuseo());
        reserva.setFecha(LocalDate.now(ZoneId.of("America/Santiago")).plusDays(2));
        reserva.setPersonas(3);
        reserva.setTotal(15000);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        return reserva;
    }

    @Test
    void crearReservaCorrectamente() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now().plusDays(2);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                3
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));
        when(reservas.personasReservadas(
                eq(10L),
                eq(fecha),
                eq(EstadoReserva.CANCELADA),
                eq(-1L)
        )).thenReturn(0L);

        when(reservas.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponse respuesta = reservaService.crear("juan@correo.com", request);

        assertEquals(10L, respuesta.museoId());
        assertEquals("Museo Historico", respuesta.museoNombre());
        assertEquals("Juan Perez", respuesta.cliente());
        assertEquals(3, respuesta.personas());
        assertEquals(15000, respuesta.total());
        assertEquals("PENDIENTE", respuesta.estado());

        verify(reservas).save(any());
    }

    @Test
    void crearReservaSinCuposSuficientesLanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now().plusDays(2);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                5
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));
        when(reservas.personasReservadas(
                eq(10L),
                eq(fecha),
                eq(EstadoReserva.CANCELADA),
                eq(-1L)
        )).thenReturn(48L);

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }

    @Test
    void crearReservaEnMuseoInactivoLanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();
        museo.setActivo(false);

        LocalDate fecha = LocalDate.now().plusDays(2);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                2
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }

    @Test
    void crearReservaConFechaPasadaLanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).minusDays(1);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                2
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }

    @Test
    void obtenerMisReservasCorrectamente() {
        Usuario usuario = crearUsuario();
        Reserva reserva = crearReserva();

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(reservas.findByUsuarioIdOrderByCreadaEnDesc(1L))
                .thenReturn(List.of(reserva));

        List<ReservaResponse> respuesta = reservaService.mias("juan@correo.com");

        assertEquals(1, respuesta.size());
        assertEquals("Juan Perez", respuesta.get(0).cliente());
        assertEquals("Museo Historico", respuesta.get(0).museoNombre());
        assertEquals(3, respuesta.get(0).personas());

        verify(reservas).findByUsuarioIdOrderByCreadaEnDesc(1L);
    }

    @Test
    void obtenerTodasLasReservasCorrectamente() {
        Reserva reserva = crearReserva();

        when(reservas.findAllByOrderByCreadaEnDesc())
                .thenReturn(List.of(reserva));

        List<ReservaResponse> respuesta = reservaService.todas();

        assertEquals(1, respuesta.size());
        assertEquals("Juan Perez", respuesta.get(0).cliente());
        assertEquals("Museo Historico", respuesta.get(0).museoNombre());
        assertEquals("PENDIENTE", respuesta.get(0).estado());

        verify(reservas).findAllByOrderByCreadaEnDesc();
    }

    @Test
    void obtenerReservaPropiaCorrectamente() {
        Reserva reserva = crearReserva();

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));

        ReservaResponse respuesta = reservaService.obtener(
                "juan@correo.com", false, 1L
        );

        assertEquals("Juan Perez", respuesta.cliente());
        assertEquals("Museo Historico", respuesta.museoNombre());
    }

    @Test
    void administradorPuedeObtenerReservaAjena() {
        Reserva reserva = crearReserva();

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));

        ReservaResponse respuesta = reservaService.obtener(
                "admin@correo.com", true, 1L
        );

        assertEquals("Juan Perez", respuesta.cliente());
    }

    @Test
    void usuarioNoPuedeObtenerReservaAjena() {
        Reserva reserva = crearReserva();

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.obtener("otro@correo.com", false, 1L)
        );
    }

    @Test
    void obtenerReservaInexistenteLanzaExcepcion() {
        when(reservas.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.obtener("juan@correo.com", false, 99L)
        );
    }

    @Test
    void actualizarReservaCorrectamente() {
        Reserva reserva = crearReserva();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(5);

        ReservaRequest request = new ReservaRequest(10L, fecha, 4);

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));
        when(reservas.personasReservadas(
                eq(10L),
                eq(fecha),
                eq(EstadoReserva.CANCELADA),
                isNull()
        )).thenReturn(0L);

        when(reservas.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponse respuesta = reservaService.actualizar(
                "juan@correo.com", 1L, request
        );

        assertEquals(4, respuesta.personas());
        assertEquals(20000, respuesta.total());
        assertEquals(fecha, respuesta.fecha());

        verify(reservas).save(reserva);
    }

    @Test
    void actualizarReservaCanceladaLanzaExcepcion() {
        Reserva reserva = crearReserva();
        reserva.setEstado(EstadoReserva.CANCELADA);

        ReservaRequest request = new ReservaRequest(
                10L,
                LocalDate.now(ZoneId.of("America/Santiago")).plusDays(5),
                2
        );

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.actualizar("juan@correo.com", 1L, request)
        );

        verify(reservas, never()).save(any());
    }

    @Test
    void cancelarReservaCorrectamente() {
        Reserva reserva = crearReserva();

        Ticket ticket = new Ticket();
        ticket.setReserva(reserva);
        ticket.setCodigo("TICKET-001");
        ticket.setEstado(EstadoTicket.VIGENTE);

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));
        when(tickets.findByReservaId(reserva.getId())).thenReturn(Optional.of(ticket));

        when(tickets.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reservas.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponse respuesta = reservaService.cancelar(
                "juan@correo.com", false, 1L
        );

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals(EstadoTicket.ANULADO, ticket.getEstado());
        assertEquals("CANCELADA", respuesta.estado());

        verify(tickets).save(ticket);
        verify(reservas).save(reserva);
    }

    @Test
    void cancelarReservaYaCanceladaLanzaExcepcion() {
        Reserva reserva = crearReserva();
        reserva.setEstado(EstadoReserva.CANCELADA);

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.cancelar("juan@correo.com", false, 1L)
        );

        verify(reservas, never()).save(any());
        verify(tickets, never()).save(any());
    }

    @Test
    void usuarioNoPuedeCancelarReservaAjena() {
        Reserva reserva = crearReserva();

        when(reservas.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.cancelar("otro@correo.com", false, 1L)
        );

        verify(reservas, never()).save(any());
        verify(tickets, never()).save(any());
    }

    @Test
    void crearReservaConMuseoInexistenteLanzaExcepcion() {
        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(2);

        ReservaRequest request = new ReservaRequest(10L, fecha, 3);

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(crearUsuario());
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }
}
