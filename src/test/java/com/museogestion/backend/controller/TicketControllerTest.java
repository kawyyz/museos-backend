
package com.museogestion.backend.controller;

import com.museogestion.backend.dto.TicketDto.TicketResponse;
import com.museogestion.backend.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService service;

    @Mock
    private Authentication auth;

    @InjectMocks
    private TicketController controller;

    @Test
    void listarMisTicketsCorrectamente() {
        TicketResponse ticket = mock(TicketResponse.class);
        List<TicketResponse> tickets = List.of(ticket);

        when(auth.getName()).thenReturn("cliente@email.com");
        when(service.mios("cliente@email.com")).thenReturn(tickets);

        List<TicketResponse> resultado = controller.mios(auth);

        assertEquals(tickets, resultado);
        verify(service).mios("cliente@email.com");
    }

    @Test
    void listarTodosLosTicketsCorrectamente() {
        TicketResponse ticket = mock(TicketResponse.class);
        List<TicketResponse> tickets = List.of(ticket);

        when(service.todos()).thenReturn(tickets);

        List<TicketResponse> resultado = controller.todos();

        assertEquals(tickets, resultado);
        verify(service).todos();
    }
}

