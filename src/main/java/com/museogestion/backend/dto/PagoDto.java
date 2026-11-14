package com.museogestion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class PagoDto {

    public record PagoRequest(
            @NotBlank String numeroTarjeta,
            @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "El formato debe ser MM/AA") String vencimiento,
            @NotBlank @Pattern(regexp = "^[0-9]{3,4}$", message = "El CVV debe tener 3 o 4 dígitos") String cvv) {}

    public record PagoResponse(
            Long pagoId,
            Long reservaId,
            int monto,
            String tarjeta,
            String estado,
            LocalDateTime pagadoEn,
            String ticketCodigo) {}
}