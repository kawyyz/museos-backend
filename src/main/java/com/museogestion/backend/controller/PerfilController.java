package com.museogestion.backend.controller;

import com.museogestion.backend.dto.PerfilDto.PerfilRequest;
import com.museogestion.backend.dto.PerfilDto.PerfilResponse;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@Tag(name = "Perfil", description = "Datos del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class PerfilController {

    private final UsuarioService service;

    public PerfilController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtiene el perfil del usuario autenticado")
    public PerfilResponse obtener(Authentication auth) {
        return aRespuesta(service.porCorreo(auth.getName()));
    }

    @PutMapping
    @Operation(summary = "Actualiza nombre, teléfono y ciudad del usuario autenticado")
    public PerfilResponse actualizar(Authentication auth, @Valid @RequestBody PerfilRequest r) {
        return aRespuesta(service.actualizarPerfil(auth.getName(), r));
    }

    private static PerfilResponse aRespuesta(Usuario u) {
        return new PerfilResponse(u.getNombre(), u.getCorreo(), u.getTelefono(), u.getCiudad(), u.getRol().name());
    }
}