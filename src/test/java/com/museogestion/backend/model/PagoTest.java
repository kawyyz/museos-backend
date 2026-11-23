
package com.museogestion.backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagoTest {

    @Test
    void crearPago() {
        Pago pago = new Pago();

        assertNotNull(pago);
        assertNull(pago.getId());
        assertNull(pago.getReserva());
        assertEquals(0, pago.getMonto());
        assertNull(pago.getUltimos4());
        assertNull(pago.getEstado());
        assertNotNull(pago.getPagadoEn());
    }

    @Test
    void modificarDatosPago() {
        Pago pago = new Pago();
        Reserva reserva = new Reserva();

        pago.setReserva(reserva);
        pago.setMonto(15000);
        pago.setUltimos4("1234");
        pago.setEstado(EstadoPago.APROBADO);

        assertSame(reserva, pago.getReserva());
        assertEquals(15000, pago.getMonto());
        assertEquals("1234", pago.getUltimos4());
        assertEquals(EstadoPago.APROBADO, pago.getEstado());
    }
}
