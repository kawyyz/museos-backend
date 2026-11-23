
package com.museogestion.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.*;

class BackendApplicationTest {

    @Test
    void ejecutarMain() {
        try (var mocked = mockStatic(SpringApplication.class)) {
            BackendApplication.main(new String[]{});

            mocked.verify(() ->
                    SpringApplication.run(
                            BackendApplication.class,
                            new String[]{}
                    )
            );
        }
    }

    @Test
    void crearBackendApplication() {
        BackendApplication aplicacion = new BackendApplication();

        assertNotNull(aplicacion);
    }
}
