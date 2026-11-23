
package com.museogestion.backend.controller;

import com.museogestion.backend.dto.PerfilDto.PerfilRequest;
import com.museogestion.backend.dto.PerfilDto.PerfilResponse;
import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilControllerTest {

    @Mock
    private UsuarioService service;

    @Mock
    private Authentication auth;

    @InjectMocks
    private PerfilController controller;

    private Usuario usuario;

    @BeforeEach
    void preparar() {
        usuario = new Usuario();
        usuario.setNombre("Juan Pérez");
        usuario.setCorreo("juan@email.com");
        usuario.setTelefono("+56912345678");
        usuario.setCiudad("Santiago");
        usuario.setRol(Rol.CLIENTE);

        when(auth.getName()).thenReturn("juan@email.com");
    }

    @Test
    void obtenerPerfilClienteCorrectamente() {
        when(service.porCorreo("juan@email.com")).thenReturn(usuario);

        PerfilResponse resultado = controller.obtener(auth);

        assertEquals("Juan Pérez", resultado.nombre());
        assertEquals("juan@email.com", resultado.correo());
        assertEquals("+56912345678", resultado.telefono());
        assertEquals("Santiago", resultado.ciudad());
        assertEquals("CLIENTE", resultado.rol());

        verify(service).porCorreo("juan@email.com");
    }

    @Test
    void obtenerPerfilAdministradorCorrectamente() {
        usuario.setRol(Rol.ADMIN);

        when(service.porCorreo("juan@email.com")).thenReturn(usuario);

        PerfilResponse resultado = controller.obtener(auth);

        assertEquals("ADMIN", resultado.rol());
    }

    @Test
    void actualizarPerfilCorrectamente() {
        PerfilRequest request = new PerfilRequest(
                "Pedro González",
                "+56987654321",
                "Valparaíso"
        );

        usuario.setNombre("Pedro González");
        usuario.setTelefono("+56987654321");
        usuario.setCiudad("Valparaíso");

        when(service.actualizarPerfil("juan@email.com", request))
                .thenReturn(usuario);

        PerfilResponse resultado = controller.actualizar(auth, request);

        assertEquals("Pedro González", resultado.nombre());
        assertEquals("juan@email.com", resultado.correo());
        assertEquals("+56987654321", resultado.telefono());
        assertEquals("Valparaíso", resultado.ciudad());
        assertEquals("CLIENTE", resultado.rol());

        verify(service).actualizarPerfil("juan@email.com", request);
    }
}
