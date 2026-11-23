package com.museogestion.backend.service;

import com.museogestion.backend.dto.TicketDto.TicketResponse;
import com.museogestion.backend.model.*;
import com.museogestion.backend.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository tickets;

    @Mock
    private UsuarioService usuarios;

    @InjectMocks
    private TicketService ticketService;

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    private Ticket crearTicket() {
        Museo museo = new Museo();
        museo.setNombre("Museo Historico");

        Reserva reserva = new Reserva();
        reserva.setMuseo(museo);
        reserva.setFecha(LocalDate.now().plusDays(2));
        reserva.setPersonas(2);
        reserva.setTotal(10000);

        Ticket ticket = new Ticket();
        ticket.setReserva(reserva);
        ticket.setCodigo("TICKET-001");
        ticket.setEstado(EstadoTicket.VIGENTE);

        return ticket;
    }

    @Test
    void obtenerMisTicketsCorrectamente() {
        Usuario usuario = crearUsuario();
        Ticket ticket = crearTicket();

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(tickets.findByReservaUsuarioIdOrderByEmitidoEnDesc(1L))
                .thenReturn(List.of(ticket));

        List<TicketResponse> respuesta = ticketService.mios("juan@correo.com");

        assertEquals(1, respuesta.size());
        assertEquals("TICKET-001", respuesta.get(0).codigo());
        assertEquals("VIGENTE", respuesta.get(0).estado());
        assertEquals("Museo Historico", respuesta.get(0).museo());
        assertEquals(2, respuesta.get(0).personas());
        assertEquals(10000, respuesta.get(0).total());

        verify(usuarios).porCorreo("juan@correo.com");
        verify(tickets).findByReservaUsuarioIdOrderByEmitidoEnDesc(1L);
    }

    @Test
    void obtenerMisTicketsSinResultados() {
        Usuario usuario = crearUsuario();

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(tickets.findByReservaUsuarioIdOrderByEmitidoEnDesc(1L))
                .thenReturn(List.of());

        List<TicketResponse> respuesta = ticketService.mios("juan@correo.com");

        assertTrue(respuesta.isEmpty());
    }

    @Test
    void obtenerTodosLosTicketsCorrectamente() {
        Ticket ticket = crearTicket();

        when(tickets.findAllByOrderByEmitidoEnDesc())
                .thenReturn(List.of(ticket));

        List<TicketResponse> respuesta = ticketService.todos();

        assertEquals(1, respuesta.size());
        assertEquals("TICKET-001", respuesta.get(0).codigo());
        assertEquals("VIGENTE", respuesta.get(0).estado());
        assertEquals("Museo Historico", respuesta.get(0).museo());

        verify(tickets).findAllByOrderByEmitidoEnDesc();
    }

    @Test
    void obtenerTodosLosTicketsSinResultados() {
        when(tickets.findAllByOrderByEmitidoEnDesc())
                .thenReturn(List.of());

        List<TicketResponse> respuesta = ticketService.todos();

        assertTrue(respuesta.isEmpty());
    }
}