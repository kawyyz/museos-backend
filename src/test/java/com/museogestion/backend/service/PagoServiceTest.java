
package com.museogestion.backend.service;

import com.museogestion.backend.dto.PagoDto.PagoRequest;
import com.museogestion.backend.dto.PagoDto.PagoResponse;
import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.exception.ReglaNegocioException;
import com.museogestion.backend.model.EstadoReserva;
import com.museogestion.backend.model.Reserva;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.PagoRepository;
import com.museogestion.backend.repository.ReservaRepository;
import com.museogestion.backend.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    @Mock
    private ReservaRepository reservas;

    @Mock
    private PagoRepository pagos;

    @Mock
    private TicketRepository tickets;

    @InjectMocks
    private PagoService pagoService;

    private Reserva crearReserva(EstadoReserva estado, LocalDate fecha, String correo) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setFecha(fecha);
        reserva.setTotal(15000);
        reserva.setEstado(estado);
        ReflectionTestUtils.setField(reserva, "id", 1L);

        return reserva;
    }

    private PagoRequest crearPagoRequest(String numeroTarjeta, YearMonth vencimiento) {
        String fecha = String.format(
                "%02d/%02d",
                vencimiento.getMonthValue(),
                vencimiento.getYear() % 100
        );

        return new PagoRequest(numeroTarjeta, fecha, "123");
    }

    @Test
    void pagarReservaCorrectamente() {
        Reserva reserva = crearReserva(
                EstadoReserva.PENDIENTE,
                LocalDate.now(ZONA).plusDays(2),
                "juan@correo.com"
        );

        PagoRequest request = crearPagoRequest(
                "4111 1111 1111 1111",
                YearMonth.now(ZONA).plusMonths(1)
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));
        when(tickets.existsByCodigo(any())).thenReturn(false);

        PagoResponse respuesta = pagoService.pagar(
                "juan@correo.com",
                1L,
                request
        );

        assertEquals(1L, respuesta.reservaId());
        assertEquals(15000, respuesta.monto());
        assertEquals("**** **** **** 1111", respuesta.tarjeta());
        assertEquals("APROBADO", respuesta.estado());
        assertNotNull(respuesta.ticketCodigo());
        assertTrue(respuesta.ticketCodigo().matches("MG-[A-F0-9]{8}"));

        assertEquals(EstadoReserva.PAGADA, reserva.getEstado());

        verify(pagos).save(any());
        verify(reservas).save(reserva);
        verify(tickets).save(any());
    }

    @Test
    void pagarReservaYaPagadaLanzaExcepcion() {
        Reserva reserva = crearReserva(
                EstadoReserva.PAGADA,
                LocalDate.now(ZONA).plusDays(2),
                "juan@correo.com"
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(
                "4111111111111111",
                YearMonth.now(ZONA).plusMonths(1)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> pagoService.pagar("juan@correo.com", 1L, request)
        );

        verifyNoInteractions(pagos, tickets);
    }

    @Test
    void pagarReservaCanceladaLanzaExcepcion() {
        Reserva reserva = crearReserva(
                EstadoReserva.CANCELADA,
                LocalDate.now(ZONA).plusDays(2),
                "juan@correo.com"
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(
                "4111111111111111",
                YearMonth.now(ZONA).plusMonths(1)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> pagoService.pagar("juan@correo.com", 1L, request)
        );

        verifyNoInteractions(pagos, tickets);
    }

    @Test
    void pagarConNumeroDeTarjetaInvalidoLanzaExcepcion() {
        Reserva reserva = crearReserva(
                EstadoReserva.PENDIENTE,
                LocalDate.now(ZONA).plusDays(2),
                "juan@correo.com"
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(
                "1234",
                YearMonth.now(ZONA).plusMonths(1)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> pagoService.pagar("juan@correo.com", 1L, request)
        );

        verifyNoInteractions(pagos, tickets);
    }

    @Test
    void pagarConTarjetaVencidaLanzaExcepcion() {
        Reserva reserva = crearReserva(
                EstadoReserva.PENDIENTE,
                LocalDate.now(ZONA).plusDays(2),
                "juan@correo.com"
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(
                "4111111111111111",
                YearMonth.now(ZONA).minusMonths(1)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> pagoService.pagar("juan@correo.com", 1L, request)
        );

        verifyNoInteractions(pagos, tickets);
    }

    @Test
    void pagarReservaConFechaPasadaLanzaExcepcion() {
        Reserva reserva = crearReserva(
                EstadoReserva.PENDIENTE,
                LocalDate.now(ZONA).minusDays(1),
                "juan@correo.com"
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(
                "4111111111111111",
                YearMonth.now(ZONA).plusMonths(1)
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> pagoService.pagar("juan@correo.com", 1L, request)
        );

        verifyNoInteractions(pagos, tickets);
    }

    @Test
    void pagarReservaDeOtroUsuarioLanzaExcepcion() {
        Reserva reserva = crearReserva(
                EstadoReserva.PENDIENTE,
                LocalDate.now(ZONA).plusDays(2),
                "otro@correo.com"
        );

        when(reservas.findByIdForUpdate(1L)).thenReturn(Optional.of(reserva));

        PagoRequest request = crearPagoRequest(
                "4111111111111111",
                YearMonth.now(ZONA).plusMonths(1)
        );

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> pagoService.pagar("juan@correo.com", 1L, request)
        );

        verifyNoInteractions(pagos, tickets);
    }
}
