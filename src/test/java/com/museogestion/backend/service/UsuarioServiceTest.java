
package com.museogestion.backend.service;

import com.museogestion.backend.dto.PerfilDto.PerfilRequest;
import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setPassword("hash123");
        usuario.setTelefono("123456789");
        usuario.setCiudad("Santiago");
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    @Test
    void buscarUsuarioPorCorreoCorrectamente() {
        Usuario usuario = crearUsuario();

        when(repo.findByCorreoIgnoreCase("juan@correo.com"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.porCorreo("juan@correo.com");

        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juan@correo.com", resultado.getCorreo());
        verify(repo).findByCorreoIgnoreCase("juan@correo.com");
    }

    @Test
    void buscarUsuarioInexistenteLanzaExcepcion() {
        when(repo.findByCorreoIgnoreCase("inexistente@correo.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> usuarioService.porCorreo("inexistente@correo.com")
        );
    }

    @Test
    void actualizarPerfilCorrectamente() {
        Usuario usuario = crearUsuario();

        PerfilRequest request = new PerfilRequest(
                "Pedro Gonzalez",
                "987654321",
                "Valparaiso"
        );

        when(repo.findByCorreoIgnoreCase("juan@correo.com"))
                .thenReturn(Optional.of(usuario));

        when(repo.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.actualizarPerfil(
                "juan@correo.com",
                request
        );

        assertEquals("Pedro Gonzalez", resultado.getNombre());
        assertEquals("987654321", resultado.getTelefono());
        assertEquals("Valparaiso", resultado.getCiudad());

        verify(repo).save(usuario);
    }

    @Test
    void actualizarPerfilUsuarioInexistenteLanzaExcepcion() {
        PerfilRequest request = new PerfilRequest(
                "Pedro Gonzalez",
                "987654321",
                "Valparaiso"
        );

        when(repo.findByCorreoIgnoreCase("inexistente@correo.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> usuarioService.actualizarPerfil(
                        "inexistente@correo.com",
                        request
                )
        );

        verify(repo, never()).save(any(Usuario.class));
    }
}
