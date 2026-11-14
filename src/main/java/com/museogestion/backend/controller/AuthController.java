package com.museogestion.backend.controller;

import com.museogestion.backend.dto.AuthDto.AuthResponse;
import com.museogestion.backend.dto.AuthDto.LoginRequest;
import com.museogestion.backend.dto.AuthDto.RefreshRequest;
import com.museogestion.backend.dto.AuthDto.RegistroRequest;
import com.museogestion.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro, login y renovación de tokens JWT")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registra un cliente nuevo y entrega sus tokens")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(r));
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión con correo y contraseña")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        return service.login(r);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Entrega un par de tokens nuevo a partir de un refresh token válido")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest r) {
        return service.refrescar(r);
    }
}