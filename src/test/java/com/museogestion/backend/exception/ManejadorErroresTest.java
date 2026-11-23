
package com.museogestion.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ManejadorErroresTest {

    private final ManejadorErrores manejador = new ManejadorErrores();

    @Test
    void recursoNoEncontradoDevuelve404() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("Museo no encontrado");

        var respuesta = manejador.noEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Museo no encontrado"), respuesta.getBody());
    }

    @Test
    void conflictoDevuelve409() {
        ConflictoException ex = new ConflictoException("Ya existe");

        var respuesta = manejador.conflicto(ex);

        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Ya existe"), respuesta.getBody());
    }

    @Test
    void credencialesInvalidasDevuelve401() {
        CredencialesInvalidasException ex =
                new CredencialesInvalidasException("Acceso denegado");

        var respuesta = manejador.credenciales(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Acceso denegado"), respuesta.getBody());
    }

    @Test
    void reglaNegocioDevuelve400() {
        ReglaNegocioException ex =
                new ReglaNegocioException("No se puede realizar la operación");

        var respuesta = manejador.reglaNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(
                Map.of("error", "No se puede realizar la operación"),
                respuesta.getBody()
        );
    }

    @Test
    void cuerpoInvalidoDevuelve400() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON inválido");

        var respuesta = manejador.cuerpoInvalido(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(
                "El cuerpo de la petición no es válido. Revisa el JSON y que las fechas usen el formato AAAA-MM-DD",
                respuesta.getBody().get("error")
        );
    }

    @Test
    void datosInvalidosDevuelveErroresPorCampo() {
        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("perfilRequest", "nombre", "El nombre es obligatorio"),
                new FieldError("perfilRequest", "telefono", "El teléfono no es válido")
        ));

        var respuesta = manejador.datosInvalidos(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
        assertEquals("El nombre es obligatorio", respuesta.getBody().get("nombre"));
        assertEquals("El teléfono no es válido", respuesta.getBody().get("telefono"));
    }

    @Test
    void credencialesInvalidasConstructorPorDefecto() {
        CredencialesInvalidasException ex = new CredencialesInvalidasException();

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void credencialesInvalidasConstructorConMensaje() {
        CredencialesInvalidasException ex =
                new CredencialesInvalidasException("Error personalizado");

        assertEquals("Error personalizado", ex.getMessage());
    }

    @Test
    void reglaNegocioConstructor() {
        ReglaNegocioException ex = new ReglaNegocioException("Regla incumplida");

        assertEquals("Regla incumplida", ex.getMessage());
    }

    @Test
    void conflictoConstructor() {
        ConflictoException ex = new ConflictoException("Conflicto detectado");

        assertEquals("Conflicto detectado", ex.getMessage());
    }

    @Test
    void recursoNoEncontradoConstructor() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("No existe");

        assertEquals("No existe", ex.getMessage());
    }
}
