package com.museogestion.backend.dto;

import com.museogestion.backend.model.Reserva;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDto {

    public record ReservaRequest(
            @NotNull Long museoId,
            @NotNull LocalDate fecha,
            @Min(value = 1, message = "Debe haber al menos 1 persona")
            @Max(value = 20, message = "El máximo es de 20 personas por reserva") int personas) {}

    public record ReservaResponse(
            Long id,
            Long museoId,
            String museoNombre,
            String cliente,
            LocalDate fecha,
            int personas,
            int total,
            String estado,
            LocalDateTime creadaEn) {

        public static ReservaResponse de(Reserva r) {
            return new ReservaResponse(
                    r.getId(),
                    r.getMuseo().getId(),
                    r.getMuseo().getNombre(),
                    r.getUsuario().getNombre(),
                    r.getFecha(),
                    r.getPersonas(),
                    r.getTotal(),
                    r.getEstado().name(),
                    r.getCreadaEn());
        }
    }
}