package com.museogestion.backend.config;

import com.museogestion.backend.controller.MuseoController;
import com.museogestion.backend.controller.ReservaController;
import com.museogestion.backend.controller.TicketController;
import com.museogestion.backend.security.JwtService;
import com.museogestion.backend.service.MuseoService;
import com.museogestion.backend.service.PagoService;
import com.museogestion.backend.service.ReservaService;
import com.museogestion.backend.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        MuseoController.class,
        ReservaController.class,
        TicketController.class
})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.cors.origins=http://localhost:5173"
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MuseoService museoService;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private PagoService pagoService;

    @MockBean
    private TicketService ticketService;

    @Test
    void permitirAccesoPublicoAMuseos() throws Exception {
        when(museoService.listar(null)).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/museos"))
                .andExpect(status().isOk());

        verify(museoService).listar(null);
    }

    @Test
    void rechazarAccesoSinTokenAReservas() throws Exception {
        mockMvc.perform(get("/api/reservas/mis-reservas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazarAccesoDeClienteAListaDeReservas() throws Exception {
        mockMvc.perform(get("/api/reservas")
                        .with(user("cliente@museos.cl").roles("CLIENTE")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(reservaService);
    }

    @Test
    void permitirAccesoAdminAListaDeReservas() throws Exception {
        when(reservaService.todas()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/reservas")
                        .with(user("admin@museos.cl").roles("ADMIN")))
                .andExpect(status().isOk());

        verify(reservaService).todas();
    }

    @Test
    void rechazarAccesoDeClienteAListaDeTickets() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .with(user("cliente@museos.cl").roles("CLIENTE")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(ticketService);
    }

    @Test
    void permitirAccesoAdminAListaDeTickets() throws Exception {
        when(ticketService.todos()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/tickets")
                        .with(user("admin@museos.cl").roles("ADMIN")))
                .andExpect(status().isOk());

        verify(ticketService).todos();
    }
}
