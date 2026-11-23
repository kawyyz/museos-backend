
package com.museogestion.backend.config;

import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuariosInicialesTest {

    @Mock
    private UsuarioRepository repo;

    @Mock
    private PasswordEncoder encoder;

    private final UsuariosIniciales usuariosIniciales = new UsuariosIniciales();

    @Test
    void cargarUsuariosCuandoNoExistenLosCrea() throws Exception {
        when(repo.existsByCorreoIgnoreCase("admin@museos.cl"))
                .thenReturn(false);
        when(repo.existsByCorreoIgnoreCase("juan.perez@email.com"))
                .thenReturn(false);

        when(encoder.encode("admin123")).thenReturn("admin-hash");
        when(encoder.encode("Cliente12345")).thenReturn("cliente-hash");

        CommandLineRunner runner = usuariosIniciales.cargarUsuarios(
                repo, encoder, "admin123"
        );

        runner.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

        verify(repo, times(2)).save(captor.capture());

        assertEquals(2, captor.getAllValues().size());

        Usuario admin = captor.getAllValues().get(0);
        assertEquals("Administrador", admin.getNombre());
        assertEquals("admin@museos.cl", admin.getCorreo());
        assertEquals("admin-hash", admin.getPassword());
        assertEquals(Rol.ADMIN, admin.getRol());

        Usuario cliente = captor.getAllValues().get(1);
        assertEquals("Juan Pérez", cliente.getNombre());
        assertEquals("juan.perez@email.com", cliente.getCorreo());
        assertEquals("cliente-hash", cliente.getPassword());
        assertEquals(Rol.CLIENTE, cliente.getRol());

        verify(encoder).encode("admin123");
        verify(encoder).encode("Cliente12345");
    }

    @Test
    void cargarUsuariosCuandoYaExistenNoLosDuplica() throws Exception {
        when(repo.existsByCorreoIgnoreCase("admin@museos.cl"))
                .thenReturn(true);
        when(repo.existsByCorreoIgnoreCase("juan.perez@email.com"))
                .thenReturn(true);

        CommandLineRunner runner = usuariosIniciales.cargarUsuarios(
                repo, encoder, "admin123"
        );

        runner.run();

        verify(repo, never()).save(any(Usuario.class));
        verifyNoInteractions(encoder);
    }
}
