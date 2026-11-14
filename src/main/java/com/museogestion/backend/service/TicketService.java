package com.museogestion.backend.service;

import com.museogestion.backend.dto.TicketDto.TicketResponse;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository tickets;
    private final UsuarioService usuarios;

    public TicketService(TicketRepository tickets, UsuarioService usuarios) {
        this.tickets = tickets;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> mios(String correo) {
        Usuario usuario = usuarios.porCorreo(correo);
        return tickets.findByReservaUsuarioIdOrderByEmitidoEnDesc(usuario.getId())
                .stream().map(TicketResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> todos() {
        return tickets.findAllByOrderByEmitidoEnDesc().stream().map(TicketResponse::de).toList();
    }
}