package com.museogestion.backend.controller;

import com.museogestion.backend.dto.PagoDto.PagoRequest;
import com.museogestion.backend.dto.PagoDto.PagoResponse;
import com.museogestion.backend.dto.ReservaDto.ReservaRequest;
import com.museogestion.backend.dto.ReservaDto.ReservaResponse;
import com.museogestion.backend.service.PagoService;
import com.museogestion.backend.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas y pagos", description = "Reserva de visitas, pago y cancelación")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final ReservaService reservas;
    private final PagoService pagos;

    public ReservaController(ReservaService reservas, PagoService pagos) {
        this.reservas = reservas;
        this.pagos = pagos;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Crea una reserva (CLIENTE). Valida fecha, museo activo y cupos disponibles")
    public ResponseEntity<ReservaResponse> crear(Authentication auth, @Valid @RequestBody ReservaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservas.crear(auth.getName(), r));
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Lista las reservas del usuario autenticado")
    public List<ReservaResponse> mias(Authentication auth) {
        return reservas.mias(auth.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todas las reservas (solo ADMIN)")
    public List<ReservaResponse> todas() {
        return reservas.todas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una reserva: el dueño o un ADMIN")
    public ReservaResponse obtener(Authentication auth, @PathVariable Long id) {
        return reservas.obtener(auth.getName(), esAdmin(auth), id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Modifica una reserva pendiente propia")
    public ReservaResponse actualizar(Authentication auth, @PathVariable Long id,
                                      @Valid @RequestBody ReservaRequest r) {
        return reservas.actualizar(auth.getName(), id, r);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela una reserva (el dueño o un ADMIN). Anula su ticket si lo tenía")
    public ReservaResponse cancelar(Authentication auth, @PathVariable Long id) {
        return reservas.cancelar(auth.getName(), esAdmin(auth), id);
    }

    @PostMapping("/{id}/pago")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Paga una reserva pendiente (simulado) y emite el ticket")
    public ResponseEntity<PagoResponse> pagar(Authentication auth, @PathVariable Long id,
                                              @Valid @RequestBody PagoRequest p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagos.pagar(auth.getName(), id, p));
    }

    private static boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}