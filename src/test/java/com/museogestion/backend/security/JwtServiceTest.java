package com.museogestion.backend.security;

import com.museogestion.backend.model.Rol;
import com.museogestion.backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "VGhpc0lzQVNlY3JldEtleUZvclRlc3RzMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void configurar() {
        jwtService = new JwtService(SECRET, 15, 7);

        usuario = new Usuario();
        usuario.setCorreo("admin@museos.cl");
        usuario.setRol(Rol.ADMIN);
    }

    @Test
    void generarAccessTokenCorrectamente() {
        String token = jwtService.generarAccessToken(usuario);

        Claims claims = jwtService.leer(token);

        assertEquals("admin@museos.cl", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol", String.class));
        assertEquals("access", claims.get("tipo", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generarRefreshTokenCorrectamente() {
        String token = jwtService.generarRefreshToken(usuario);

        Claims claims = jwtService.leer(token);

        assertEquals("admin@museos.cl", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol", String.class));
        assertEquals("refresh", claims.get("tipo", String.class));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void obtenerDuracionAccessEnSegundos() {
        assertEquals(900, jwtService.getAccessSegundos());
    }

    @Test
    void generarTokenConSecretoVacio() {
        JwtService servicio = new JwtService("", 15, 7);

        String token = servicio.generarAccessToken(usuario);

        Claims claims = servicio.leer(token);

        assertEquals("admin@museos.cl", claims.getSubject());
        assertEquals("access", claims.get("tipo", String.class));
    }

    @Test
    void generarTokenConSecretoNulo() {
        JwtService servicio = new JwtService(null, 15, 7);

        String token = servicio.generarAccessToken(usuario);

        Claims claims = servicio.leer(token);

        assertEquals("admin@museos.cl", claims.getSubject());
    }

    @Test
    void rechazarTokenConFirmaIncorrecta() {
        JwtService otroServicio = new JwtService(
                "QW5vdGhlclNlY3JldEtleUZvclRlc3RzMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=",
                15,
                7
        );

        String token = jwtService.generarAccessToken(usuario);

        assertThrows(JwtException.class, () -> otroServicio.leer(token));
    }

    @Test
    void rechazarTokenMalformado() {
        assertThrows(JwtException.class,
                () -> jwtService.leer("esto-no-es-un-jwt"));
    }

    @Test
    void rechazarTokenVencido() {
        JwtService servicio = new JwtService(SECRET, -1, 7);

        String token = servicio.generarAccessToken(usuario);

        assertThrows(JwtException.class, () -> servicio.leer(token));
    }
}
