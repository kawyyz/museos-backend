
package com.museogestion.backend.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LatidoTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private Latido latido;

    @Test
    void latirCuandoBaseDatosFunciona() {
        when(jdbc.queryForObject("select 1", Integer.class))
                .thenReturn(1);

        latido.latir();

        verify(jdbc).queryForObject("select 1", Integer.class);
    }

    @Test
    void latirCuandoBaseDatosFalla() {
        when(jdbc.queryForObject("select 1", Integer.class))
                .thenThrow(new RuntimeException("Error de conexión"));

        latido.latir();

        verify(jdbc).queryForObject("select 1", Integer.class);
    }
}
