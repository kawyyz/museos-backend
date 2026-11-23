
package com.museogestion.backend.dto;

import com.museogestion.backend.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DtoTest {

    @Test
    void crearReservaRequest() {
        var dto = new ReservaDto.ReservaRequest(
                1L, LocalDate.of(2026, 12, 10), 3
        );

        assertEquals(1L, dto.museoId());
        assertEquals(LocalDate.of(2026, 12, 10), dto.fecha());
        assertEquals(3, dto.personas());
    }

    @Test
    void convertirReservaAReservaResponse() {
        Reserva reserva = mock(Reserva.class);
        Museo museo = mock(Museo.class);
        Usuario usuario = mock(Usuario.class);

        LocalDate fecha = LocalDate.of(2026, 12, 10);
        LocalDateTime creadaEn = LocalDateTime.of(2026, 11, 23, 10, 30);

        when(reserva.getId()).thenReturn(1L);
        when(reserva.getMuseo()).thenReturn(museo);
        when(reserva.getUsuario()).thenReturn(usuario);
        when(reserva.getFecha()).thenReturn(fecha);
        when(reserva.getPersonas()).thenReturn(3);
        when(reserva.getTotal()).thenReturn(15000);
        when(reserva.getEstado()).thenReturn(EstadoReserva.PAGADA);
        when(reserva.getCreadaEn()).thenReturn(creadaEn);

        when(museo.getId()).thenReturn(2L);
        when(museo.getNombre()).thenReturn("Museo Histórico");
        when(usuario.getNombre()).thenReturn("Juan Pérez");

        var dto = ReservaDto.ReservaResponse.de(reserva);

        assertEquals(1L, dto.id());
        assertEquals(2L, dto.museoId());
        assertEquals("Museo Histórico", dto.museoNombre());
        assertEquals("Juan Pérez", dto.cliente());
        assertEquals(fecha, dto.fecha());
        assertEquals(3, dto.personas());
        assertEquals(15000, dto.total());
        assertEquals("PAGADA", dto.estado());
        assertEquals(creadaEn, dto.creadaEn());
    }

    @Test
    void crearPagoRequest() {
        var dto = new PagoDto.PagoRequest("1234567890123456", "12/28", "123");

        assertEquals("1234567890123456", dto.numeroTarjeta());
        assertEquals("12/28", dto.vencimiento());
        assertEquals("123", dto.cvv());
    }

    @Test
    void crearPagoResponse() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 23, 11, 0);

        var dto = new PagoDto.PagoResponse(
                1L, 2L, 15000, "****1234",
                "APROBADO", fecha, "TICKET-001"
        );

        assertEquals(1L, dto.pagoId());
        assertEquals(2L, dto.reservaId());
        assertEquals(15000, dto.monto());
        assertEquals("****1234", dto.tarjeta());
        assertEquals("APROBADO", dto.estado());
        assertEquals(fecha, dto.pagadoEn());
        assertEquals("TICKET-001", dto.ticketCodigo());
    }

    @Test
    void convertirTicketATicketResponse() {
        Ticket ticket = mock(Ticket.class);
        Reserva reserva = mock(Reserva.class);
        Museo museo = mock(Museo.class);

        LocalDate fecha = LocalDate.of(2026, 12, 10);
        LocalDateTime emitidoEn = LocalDateTime.of(2026, 11, 23, 12, 0);

        when(ticket.getId()).thenReturn(1L);
        when(ticket.getCodigo()).thenReturn("TICKET-001");
        when(ticket.getEstado()).thenReturn(EstadoTicket.VIGENTE);
        when(ticket.getReserva()).thenReturn(reserva);
        when(ticket.getEmitidoEn()).thenReturn(emitidoEn);

        when(reserva.getId()).thenReturn(2L);
        when(reserva.getMuseo()).thenReturn(museo);
        when(reserva.getFecha()).thenReturn(fecha);
        when(reserva.getPersonas()).thenReturn(3);
        when(reserva.getTotal()).thenReturn(15000);

        when(museo.getNombre()).thenReturn("Museo Histórico");

        var dto = TicketDto.TicketResponse.de(ticket);

        assertEquals(1L, dto.id());
        assertEquals("TICKET-001", dto.codigo());
        assertEquals("VIGENTE", dto.estado());
        assertEquals(2L, dto.reservaId());
        assertEquals("Museo Histórico", dto.museo());
        assertEquals(fecha, dto.fecha());
        assertEquals(3, dto.personas());
        assertEquals(15000, dto.total());
        assertEquals(emitidoEn, dto.emitidoEn());
    }

    @Test
    void crearRegistroRequest() {
        var dto = new AuthDto.RegistroRequest(
                "Juan Pérez", "juan@email.com",
                "Password123", "912345678", "Santiago"
        );

        assertEquals("Juan Pérez", dto.nombre());
        assertEquals("juan@email.com", dto.correo());
        assertEquals("Password123", dto.password());
        assertEquals("912345678", dto.telefono());
        assertEquals("Santiago", dto.ciudad());
    }

    @Test
    void crearLoginRequest() {
        var dto = new AuthDto.LoginRequest("juan@email.com", "Password123");

        assertEquals("juan@email.com", dto.correo());
        assertEquals("Password123", dto.password());
    }

    @Test
    void crearRefreshRequest() {
        var dto = new AuthDto.RefreshRequest("refresh-token");

        assertEquals("refresh-token", dto.refreshToken());
    }

    @Test
    void crearAuthResponse() {
        var dto = new AuthDto.AuthResponse(
                "access", "refresh", "Bearer",
                3600L, "Juan Pérez", "juan@email.com", "CLIENTE"
        );

        assertEquals("access", dto.accessToken());
        assertEquals("refresh", dto.refreshToken());
        assertEquals("Bearer", dto.tipo());
        assertEquals(3600L, dto.expiraEnSegundos());
        assertEquals("Juan Pérez", dto.nombre());
        assertEquals("juan@email.com", dto.correo());
        assertEquals("CLIENTE", dto.rol());
    }

    @Test
    void crearPerfilRequest() {
        var dto = new PerfilDto.PerfilRequest(
                "Juan Pérez", "912345678", "Santiago"
        );

        assertEquals("Juan Pérez", dto.nombre());
        assertEquals("912345678", dto.telefono());
        assertEquals("Santiago", dto.ciudad());
    }

    @Test
    void crearPerfilResponse() {
        var dto = new PerfilDto.PerfilResponse(
                "Juan Pérez", "juan@email.com",
                "912345678", "Santiago", "CLIENTE"
        );

        assertEquals("Juan Pérez", dto.nombre());
        assertEquals("juan@email.com", dto.correo());
        assertEquals("912345678", dto.telefono());
        assertEquals("Santiago", dto.ciudad());
        assertEquals("CLIENTE", dto.rol());
    }

    @Test
    void crearClasesDto() {
        assertNotNull(new PagoDto());
        assertNotNull(new ReservaDto());
        assertNotNull(new TicketDto());
        assertNotNull(new PerfilDto());
        assertNotNull(new AuthDto());
    }
}
