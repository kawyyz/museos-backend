package com.museogestion.backend.controller;

import com.museogestion.backend.dto.TicketDto.TicketResponse;
import com.museogestion.backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Entradas emitidas al pagar una reserva")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping("/mis-tickets")
    @Operation(summary = "Lista los tickets del usuario autenticado")
    public List<TicketResponse> mios(Authentication auth) {
        return service.mios(auth.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos los tickets (solo ADMIN)")
    public List<TicketResponse> todos() {
        return service.todos();
    }
}