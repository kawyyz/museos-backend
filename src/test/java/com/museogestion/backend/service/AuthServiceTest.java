
package com.museogestion.backend.service;

import com.museogestion.backend.dto.AuthDto.AuthResponse;
import com.museogestion.backend.dto.AuthDto.LoginRequest;
import com.museogestion.backend.dto.AuthDto.RegistroRequest;
import com.museogestion.backend.exception.ConflictoException;
import com.museogestion.backend.exception.CredencialesInvalidasException;
import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.repository.UsuarioRepository;
import com.museogestion.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.museogestion.backend.dto.AuthDto.RefreshRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.mockito.Mockito;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrarUsuarioCorrectamente() {
        RegistroRequest request = new RegistroRequest(
                "Juan Perez",
                " JUAN@CORREO.COM ",
                "password123",
                "123456789",
                "Santiago"
        );

        when(repo.existsByCorreoIgnoreCase("juan@correo.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("hash123");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        when(jwt.generarAccessToken(any(Usuario.class))).thenReturn("access-token");
        when(jwt.generarRefreshToken(any(Usuario.class))).thenReturn("refresh-token");
        when(jwt.getAccessSegundos()).thenReturn(900L);

        AuthResponse respuesta = authService.registrar(request);

        assertEquals("Juan Perez", respuesta.nombre());
        assertEquals("juan@correo.com", respuesta.correo());
        assertEquals("CLIENTE", respuesta.rol());
        assertEquals("Bearer", respuesta.tipo());
        assertEquals("access-token", respuesta.accessToken());

        verify(repo).save(any(Usuario.class));
        verify(encoder).encode("password123");
    }

    @Test
    void registrarCorreoDuplicadoLanzaExcepcion() {
        RegistroRequest request = new RegistroRequest(
                "Juan Perez",
                "juan@correo.com",
                "password123",
                "123456789",
                "Santiago"
        );

        when(repo.existsByCorreoIgnoreCase("juan@correo.com")).thenReturn(true);

        assertThrows(
                ConflictoException.class,
                () -> authService.registrar(request)
        );

        verify(repo, never()).save(any(Usuario.class));
    }

    @Test
    void iniciarSesionCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setPassword("hash123");
        usuario.setRol(Rol.CLIENTE);

        LoginRequest request = new LoginRequest(
                "juan@correo.com",
                "password123"
        );

        when(repo.findByCorreoIgnoreCase("juan@correo.com"))
                .thenReturn(Optional.of(usuario));
        when(encoder.matches("password123", "hash123")).thenReturn(true);

        when(jwt.generarAccessToken(usuario)).thenReturn("access-token");
        when(jwt.generarRefreshToken(usuario)).thenReturn("refresh-token");
        when(jwt.getAccessSegundos()).thenReturn(900L);

        AuthResponse respuesta = authService.login(request);

        assertEquals("Juan Perez", respuesta.nombre());
        assertEquals("juan@correo.com", respuesta.correo());
        assertEquals("CLIENTE", respuesta.rol());
        assertEquals("access-token", respuesta.accessToken());
    }

    @Test
    void iniciarSesionConContrasenaIncorrectaLanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("juan@correo.com");
        usuario.setPassword("hash123");
        usuario.setRol(Rol.CLIENTE);

        LoginRequest request = new LoginRequest(
                "juan@correo.com",
                "incorrecta"
        );

        when(repo.findByCorreoIgnoreCase("juan@correo.com"))
                .thenReturn(Optional.of(usuario));
        when(encoder.matches("incorrecta", "hash123")).thenReturn(false);

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void iniciarSesionConCorreoInexistenteLanzaExcepcion() {
        LoginRequest request = new LoginRequest(
                "inexistente@correo.com",
                "password123"
        );

        when(repo.findByCorreoIgnoreCase("inexistente@correo.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.login(request)
        );

        verify(encoder, never()).matches(any(), any());
    }

    @Test
    void refrescarConTokenInvalidoLanzaExcepcion() {
        RefreshRequest request = new RefreshRequest("token-invalido");

        when(jwt.leer("token-invalido"))
                .thenThrow(new JwtException("Token inválido"));

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.refrescar(request)
        );
    }

    @Test
    void refrescarConTokenQueNoEsRefreshLanzaExcepcion() {
        RefreshRequest request = new RefreshRequest("access-token");

        Claims claims = Mockito.mock(Claims.class);

        when(jwt.leer("access-token")).thenReturn(claims);
        when(claims.get("tipo", String.class)).thenReturn("access");

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.refrescar(request)
        );
    }

    @Test
    void refrescarConUsuarioInexistenteLanzaExcepcion() {
        RefreshRequest request = new RefreshRequest("refresh-token");

        Claims claims = Mockito.mock(Claims.class);

        when(jwt.leer("refresh-token")).thenReturn(claims);
        when(claims.get("tipo", String.class)).thenReturn("refresh");
        when(claims.getSubject()).thenReturn("inexistente@correo.com");

        when(repo.findByCorreoIgnoreCase("inexistente@correo.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.refrescar(request)
        );
    }

    @Test
    void refrescarTokenCorrectamente() {
        RefreshRequest request = new RefreshRequest("refresh-token");

        Usuario usuario = new Usuario();
        usuario.setNombre("Juan Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setPassword("hash123");
        usuario.setRol(Rol.CLIENTE);

        Claims claims = Mockito.mock(Claims.class);

        when(jwt.leer("refresh-token")).thenReturn(claims);
        when(claims.get("tipo", String.class)).thenReturn("refresh");
        when(claims.getSubject()).thenReturn("juan@correo.com");

        when(repo.findByCorreoIgnoreCase("juan@correo.com"))
                .thenReturn(Optional.of(usuario));

        when(jwt.generarAccessToken(usuario)).thenReturn("nuevo-access-token");
        when(jwt.generarRefreshToken(usuario)).thenReturn("nuevo-refresh-token");
        when(jwt.getAccessSegundos()).thenReturn(900L);

        AuthResponse respuesta = authService.refrescar(request);

        assertEquals("nuevo-access-token", respuesta.accessToken());
        assertEquals("nuevo-refresh-token", respuesta.refreshToken());
        assertEquals("Juan Perez", respuesta.nombre());
        assertEquals("juan@correo.com", respuesta.correo());
        assertEquals("CLIENTE", respuesta.rol());
    }
}
