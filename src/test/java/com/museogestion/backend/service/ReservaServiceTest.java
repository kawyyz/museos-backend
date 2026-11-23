
package com.museogestion.backend.service;

import com.museogestion.backend.dto.ReservaDto.ReservaRequest;
import com.museogestion.backend.dto.ReservaDto.ReservaResponse;
import com.museogestion.backend.exception.ReglaNegocioException;
import com.museogestion.backend.model.EstadoReserva;
import com.museogestion.backend.model.Museo;
import com.museogestion.backend.model.Usuario;
import com.museogestion.backend.model.Rol;
import com.museogestion.backend.repository.MuseoRepository;
import com.museogestion.backend.repository.ReservaRepository;
import com.museogestion.backend.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservas;

    @Mock
    private MuseoRepository museos;

    @Mock
    private TicketRepository tickets;

    @Mock
    private UsuarioService usuarios;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setCorreo("juan@correo.com");
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    private Museo crearMuseo() {
        Museo museo = new Museo();
        museo.setId(10L);
        museo.setNombre("Museo Historico");
        museo.setPrecioEntrada(5000);
        museo.setCapacidadDiaria(50);
        museo.setActivo(true);
        return museo;
    }

    @Test
    void crearReservaCorrectamente() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now().plusDays(2);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                3
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));
        when(reservas.personasReservadas(
                eq(10L),
                eq(fecha),
                eq(EstadoReserva.CANCELADA),
                eq(-1L)
        )).thenReturn(0L);

        when(reservas.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponse respuesta = reservaService.crear("juan@correo.com", request);

        assertEquals(10L, respuesta.museoId());
        assertEquals("Museo Historico", respuesta.museoNombre());
        assertEquals("Juan Perez", respuesta.cliente());
        assertEquals(3, respuesta.personas());
        assertEquals(15000, respuesta.total());
        assertEquals("PENDIENTE", respuesta.estado());

        verify(reservas).save(any());
    }

    @Test
    void crearReservaSinCuposSuficientesLanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now().plusDays(2);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                5
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));
        when(reservas.personasReservadas(
                eq(10L),
                eq(fecha),
                eq(EstadoReserva.CANCELADA),
                eq(-1L)
        )).thenReturn(48L);

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }

    @Test
    void crearReservaEnMuseoInactivoLanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();
        museo.setActivo(false);

        LocalDate fecha = LocalDate.now().plusDays(2);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                2
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }

    @Test
    void crearReservaConFechaPasadaLanzaExcepcion() {
        Usuario usuario = crearUsuario();
        Museo museo = crearMuseo();

        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).minusDays(1);

        ReservaRequest request = new ReservaRequest(
                10L,
                fecha,
                2
        );

        when(usuarios.porCorreo("juan@correo.com")).thenReturn(usuario);
        when(museos.findByIdForUpdate(10L)).thenReturn(Optional.of(museo));

        assertThrows(
                ReglaNegocioException.class,
                () -> reservaService.crear("juan@correo.com", request)
        );

        verify(reservas, never()).save(any());
    }
}
