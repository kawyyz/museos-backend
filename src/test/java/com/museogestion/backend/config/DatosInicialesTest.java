package com.museogestion.backend.config;

import com.museogestion.backend.model.Museo;
import com.museogestion.backend.repository.MuseoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.Mockito.*;

class DatosInicialesTest {

    private final DatosIniciales datosIniciales = new DatosIniciales();

    @Test
    void noCrearMuseosCuandoYaExisten() throws Exception {
        MuseoRepository repo = mock(MuseoRepository.class);

        when(repo.count()).thenReturn(1L);

        CommandLineRunner runner = datosIniciales.cargarMuseos(repo);
        runner.run(new String[]{});

        verify(repo).count();
        verify(repo, never()).save(any(Museo.class));
    }

    @Test
    void crearCuatroMuseosCuandoNoExisten() throws Exception {
        MuseoRepository repo = mock(MuseoRepository.class);

        when(repo.count()).thenReturn(0L);

        CommandLineRunner runner = datosIniciales.cargarMuseos(repo);
        runner.run(new String[]{});

        verify(repo).count();
        verify(repo, times(4)).save(any(Museo.class));
    }
}
