package com.museogestion.backend.service;

import com.museogestion.backend.exception.RecursoNoEncontradoException;
import com.museogestion.backend.model.Museo;
import com.museogestion.backend.repository.MuseoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseoServiceTest {

    @Mock
    private MuseoRepository repo;

    @InjectMocks
    private MuseoService museoService;

    private Museo crearMuseo() {
        Museo museo = new Museo();
        museo.setId(1L);
        museo.setNombre("Museo Historico");
        museo.setSigla("MHI");
        museo.setZona("Santiago");
        museo.setDescripcion("Museo de historia");
        museo.setPrecioEntrada(5000);
        museo.setCapacidadDiaria(50);
        museo.setActivo(true);
        return museo;
    }

    @Test
    void listarTodosLosMuseos() {
        when(repo.findAll()).thenReturn(List.of(crearMuseo()));

        List<Museo> respuesta = museoService.listar(null);

        assertEquals(1, respuesta.size());
        assertEquals("Museo Historico", respuesta.get(0).getNombre());

        verify(repo).findAll();
    }

    @Test
    void listarMuseosConBusquedaVacia() {
        when(repo.findAll()).thenReturn(List.of(crearMuseo()));

        List<Museo> respuesta = museoService.listar("   ");

        assertEquals(1, respuesta.size());
        verify(repo).findAll();
    }

    @Test
    void buscarMuseosPorNombreOZona() {
        when(repo.findByNombreContainingIgnoreCaseOrZonaContainingIgnoreCase(
                "Santiago", "Santiago"
        )).thenReturn(List.of(crearMuseo()));

        List<Museo> respuesta = museoService.listar("Santiago");

        assertEquals(1, respuesta.size());
        verify(repo).findByNombreContainingIgnoreCaseOrZonaContainingIgnoreCase(
                "Santiago", "Santiago"
        );
    }

    @Test
    void obtenerMuseoCorrectamente() {
        Museo museo = crearMuseo();

        when(repo.findById(1L)).thenReturn(Optional.of(museo));

        Museo respuesta = museoService.obtener(1L);

        assertEquals("Museo Historico", respuesta.getNombre());
        assertEquals(1L, respuesta.getId());
    }

    @Test
    void obtenerMuseoInexistenteLanzaExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> museoService.obtener(1L)
        );
    }

    @Test
    void crearMuseoCorrectamente() {
        Museo museo = crearMuseo();

        when(repo.save(any(Museo.class))).thenAnswer(inv -> inv.getArgument(0));

        Museo respuesta = museoService.crear(museo);

        assertNull(respuesta.getId());
        assertEquals("Museo Historico", respuesta.getNombre());

        verify(repo).save(museo);
    }

    @Test
    void actualizarMuseoCorrectamente() {
        Museo existente = crearMuseo();

        Museo datos = new Museo();
        datos.setNombre("Museo Nacional");
        datos.setSigla("MNA");
        datos.setZona("Valparaiso");
        datos.setDescripcion("Nueva descripcion");
        datos.setPrecioEntrada(7000);
        datos.setCapacidadDiaria(80);
        datos.setActivo(false);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Museo.class))).thenAnswer(inv -> inv.getArgument(0));

        Museo respuesta = museoService.actualizar(1L, datos);

        assertEquals("Museo Nacional", respuesta.getNombre());
        assertEquals("MNA", respuesta.getSigla());
        assertEquals("Valparaiso", respuesta.getZona());
        assertEquals("Nueva descripcion", respuesta.getDescripcion());
        assertEquals(7000, respuesta.getPrecioEntrada());
        assertEquals(80, respuesta.getCapacidadDiaria());
        assertFalse(respuesta.isActivo());

        verify(repo).save(existente);
    }

    @Test
    void actualizarMuseoInexistenteLanzaExcepcion() {
        Museo datos = crearMuseo();

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> museoService.actualizar(1L, datos)
        );

        verify(repo, never()).save(any());
    }

    @Test
    void eliminarMuseoCorrectamente() {
        when(repo.findById(1L)).thenReturn(Optional.of(crearMuseo()));

        assertDoesNotThrow(() -> museoService.eliminar(1L));

        verify(repo).deleteById(1L);
    }

    @Test
    void eliminarMuseoInexistenteLanzaExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> museoService.eliminar(1L)
        );

        verify(repo, never()).deleteById(any());
    }
}