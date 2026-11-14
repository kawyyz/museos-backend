package com.museogestion.backend.service;

import com.museogestion.backend.dto.PerfilDto.PerfilRequest;
import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario porCorreo(String correo) {
        return repo.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + correo));
    }

    public Usuario actualizarPerfil(String correo, PerfilRequest r) {
        Usuario u = porCorreo(correo);
        u.setNombre(r.nombre().trim());
        u.setTelefono(r.telefono());
        u.setCiudad(r.ciudad());
        return repo.save(u);
    }
}