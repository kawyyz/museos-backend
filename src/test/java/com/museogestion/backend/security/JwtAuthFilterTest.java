package com.museogestion.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private JwtAuthFilter filtro;
    private FilterChain chain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void configurar() {
        jwtService = mock(JwtService.class);
        filtro = new JwtAuthFilter(jwtService);
        chain = mock(FilterChain.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void continuarCuandoNoHayAuthorization() throws Exception {
        filtro.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void continuarCuandoAuthorizationNoEsBearer() throws Exception {
        request.addHeader("Authorization", "Basic abc123");

        filtro.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void autenticarCuandoTokenEsAccess() throws Exception {
        Claims claims = mock(Claims.class);

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtService.leer("token-valido")).thenReturn(claims);
        when(claims.get("tipo", String.class)).thenReturn("access");
        when(claims.getSubject()).thenReturn("admin@museos.cl");
        when(claims.get("rol", String.class)).thenReturn("ADMIN");

        filtro.doFilterInternal(request, response, chain);

        var autenticacion = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(autenticacion);
        assertEquals("admin@museos.cl", autenticacion.getName());
        assertTrue(autenticacion.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(chain).doFilter(request, response);
    }

    @Test
    void noAutenticarCuandoTokenEsRefresh() throws Exception {
        Claims claims = mock(Claims.class);

        request.addHeader("Authorization", "Bearer refresh-token");

        when(jwtService.leer("refresh-token")).thenReturn(claims);
        when(claims.get("tipo", String.class)).thenReturn("refresh");

        filtro.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void continuarCuandoTokenEsInvalido() throws Exception {
        request.addHeader("Authorization", "Bearer token-invalido");

        when(jwtService.leer("token-invalido"))
                .thenThrow(new JwtException("Token inválido"));

        filtro.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void continuarCuandoOcurreIllegalArgumentException() throws Exception {
        request.addHeader("Authorization", "Bearer token-vacio");

        when(jwtService.leer("token-vacio"))
                .thenThrow(new IllegalArgumentException("Token vacío"));

        filtro.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
