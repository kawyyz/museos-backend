
package com.museogestion.backend.controller;

import com.museogestion.backend.dto.AuthDto.AuthResponse;
import com.museogestion.backend.dto.AuthDto.LoginRequest;
import com.museogestion.backend.dto.AuthDto.RefreshRequest;
import com.museogestion.backend.dto.AuthDto.RegistroRequest;
import com.museogestion.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService service;

    @InjectMocks
    private AuthController controller;

    private AuthResponse respuesta;

    @BeforeEach
    void preparar() {
        respuesta = mock(AuthResponse.class);
    }

    @Test
    void registroCorrectamente() {
        RegistroRequest request = mock(RegistroRequest.class);
        when(service.registrar(request)).thenReturn(respuesta);

        ResponseEntity<AuthResponse> resultado = controller.registro(request);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertSame(respuesta, resultado.getBody());
        verify(service).registrar(request);
    }

    @Test
    void loginCorrectamente() {
        LoginRequest request = mock(LoginRequest.class);
        when(service.login(request)).thenReturn(respuesta);

        AuthResponse resultado = controller.login(request);

        assertSame(respuesta, resultado);
        verify(service).login(request);
    }

    @Test
    void refreshCorrectamente() {
        RefreshRequest request = mock(RefreshRequest.class);
        when(service.refrescar(request)).thenReturn(respuesta);

        AuthResponse resultado = controller.refresh(request);

        assertSame(respuesta, resultado);
        verify(service).refrescar(request);
    }
}

