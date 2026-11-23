
package com.museogestion.backend.controller;

import com.museogestion.backend.model.Museo;
import com.museogestion.backend.service.MuseoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseoControllerTest {

    @Mock
    private MuseoService service;

    @InjectMocks
    private MuseoController controller;

    @Test
    void listarMuseosSinBusqueda() {
        List<Museo> museos = List.of(mock(Museo.class), mock(Museo.class));
        when(service.listar(null)).thenReturn(museos);

        List<Museo> resultado = controller.listar(null);

        assertEquals(2, resultado.size());
        assertSame(museos, resultado);
        verify(service).listar(null);
    }

    @Test
    void listarMuseosConBusqueda() {
        List<Museo> museos = List.of(mock(Museo.class));
        when(service.listar("historia")).thenReturn(museos);

        List<Museo> resultado = controller.listar("historia");

        assertEquals(1, resultado.size());
        assertSame(museos, resultado);
        verify(service).listar("historia");
    }

    @Test
    void obtenerMuseoCorrectamente() {
        Museo museo = mock(Museo.class);
        when(service.obtener(1L)).thenReturn(museo);

        Museo resultado = controller.obtener(1L);

        assertSame(museo, resultado);
        verify(service).obtener(1L);
    }

    @Test
    void crearMuseoCorrectamente() {
        Museo museo = mock(Museo.class);
        when(service.crear(museo)).thenReturn(museo);

        ResponseEntity<Museo> resultado = controller.crear(museo);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertSame(museo, resultado.getBody());
        verify(service).crear(museo);
    }

    @Test
    void actualizarMuseoCorrectamente() {
        Museo museo = mock(Museo.class);
        when(service.actualizar(1L, museo)).thenReturn(museo);

        Museo resultado = controller.actualizar(1L, museo);

        assertSame(museo, resultado);
        verify(service).actualizar(1L, museo);
    }

    @Test
    void eliminarMuseoCorrectamente() {
        doNothing().when(service).eliminar(1L);

        ResponseEntity<Void> resultado = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());
        assertNull(resultado.getBody());
        verify(service).eliminar(1L);
    }
}
