
package com.museogestion.backend.controller;

import com.museogestion.backend.dto.PagoDto.PagoRequest;
import com.museogestion.backend.dto.PagoDto.PagoResponse;
import com.museogestion.backend.dto.ReservaDto.ReservaRequest;
import com.museogestion.backend.dto.ReservaDto.ReservaResponse;
import com.museogestion.backend.service.PagoService;
import com.museogestion.backend.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock
    private ReservaService reservas;

    @Mock
    private PagoService pagos;

    @InjectMocks
    private ReservaController controller;

    private Authentication autenticacion(String correo, String rol) {
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn(correo);

        if (rol != null) {
            doReturn(List.of(
                    new SimpleGrantedAuthority("ROLE_" + rol)
            )).when(auth).getAuthorities();
        }

        return auth;
    }

    @Test
    void crearReservaCorrectamente() {
        Authentication auth = autenticacion("cliente@email.com", null);
        ReservaRequest request = mock(ReservaRequest.class);
        ReservaResponse respuesta = mock(ReservaResponse.class);

        when(reservas.crear("cliente@email.com", request)).thenReturn(respuesta);

        ResponseEntity<ReservaResponse> resultado = controller.crear(auth, request);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertSame(respuesta, resultado.getBody());
        verify(reservas).crear("cliente@email.com", request);
    }

    @Test
    void listarMisReservasCorrectamente() {
        Authentication auth = autenticacion("cliente@email.com", null);
        List<ReservaResponse> respuesta = List.of(mock(ReservaResponse.class));

        when(reservas.mias("cliente@email.com")).thenReturn(respuesta);

        List<ReservaResponse> resultado = controller.mias(auth);

        assertSame(respuesta, resultado);
        verify(reservas).mias("cliente@email.com");
    }

    @Test
    void listarTodasLasReservasCorrectamente() {
        List<ReservaResponse> respuesta = List.of(mock(ReservaResponse.class));

        when(reservas.todas()).thenReturn(respuesta);

        List<ReservaResponse> resultado = controller.todas();

        assertSame(respuesta, resultado);
        verify(reservas).todas();
    }

    @Test
    void obtenerReservaComoCliente() {
        Authentication auth = autenticacion("cliente@email.com", null);
        ReservaResponse respuesta = mock(ReservaResponse.class);

        when(reservas.obtener("cliente@email.com", false, 1L))
                .thenReturn(respuesta);

        ReservaResponse resultado = controller.obtener(auth, 1L);

        assertSame(respuesta, resultado);
        verify(reservas).obtener("cliente@email.com", false, 1L);
    }

    @Test
    void obtenerReservaComoAdministrador() {
        Authentication auth = autenticacion("admin@museos.cl", "ADMIN");
        ReservaResponse respuesta = mock(ReservaResponse.class);

        when(reservas.obtener("admin@museos.cl", true, 1L))
                .thenReturn(respuesta);

        ReservaResponse resultado = controller.obtener(auth, 1L);

        assertSame(respuesta, resultado);
        verify(reservas).obtener("admin@museos.cl", true, 1L);
    }

    @Test
    void actualizarReservaCorrectamente() {
        Authentication auth = autenticacion("cliente@email.com", null);
        ReservaRequest request = mock(ReservaRequest.class);
        ReservaResponse respuesta = mock(ReservaResponse.class);

        when(reservas.actualizar("cliente@email.com", 1L, request))
                .thenReturn(respuesta);

        ReservaResponse resultado = controller.actualizar(auth, 1L, request);

        assertSame(respuesta, resultado);
        verify(reservas).actualizar("cliente@email.com", 1L, request);
    }

    @Test
    void cancelarReservaComoCliente() {
        Authentication auth = autenticacion("cliente@email.com", null);
        ReservaResponse respuesta = mock(ReservaResponse.class);

        when(reservas.cancelar("cliente@email.com", false, 1L))
                .thenReturn(respuesta);

        ReservaResponse resultado = controller.cancelar(auth, 1L);

        assertSame(respuesta, resultado);
        verify(reservas).cancelar("cliente@email.com", false, 1L);
    }

    @Test
    void cancelarReservaComoAdministrador() {
        Authentication auth = autenticacion("admin@museos.cl", "ADMIN");
        ReservaResponse respuesta = mock(ReservaResponse.class);

        when(reservas.cancelar("admin@museos.cl", true, 1L))
                .thenReturn(respuesta);

        ReservaResponse resultado = controller.cancelar(auth, 1L);

        assertSame(respuesta, resultado);
        verify(reservas).cancelar("admin@museos.cl", true, 1L);
    }

    @Test
    void pagarReservaCorrectamente() {
        Authentication auth = autenticacion("cliente@email.com", null);
        PagoRequest request = mock(PagoRequest.class);
        PagoResponse respuesta = mock(PagoResponse.class);

        when(pagos.pagar("cliente@email.com", 1L, request))
                .thenReturn(respuesta);

        ResponseEntity<PagoResponse> resultado = controller.pagar(auth, 1L, request);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertSame(respuesta, resultado.getBody());
        verify(pagos).pagar("cliente@email.com", 1L, request);
    }
}
