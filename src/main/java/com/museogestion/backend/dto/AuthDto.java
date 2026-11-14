package com.museogestion.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDto {

    public record RegistroRequest(
            @NotBlank String nombre,
            @NotBlank @Email String correo,
            @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
            String telefono,
            String ciudad) {}

    public record LoginRequest(
            @NotBlank @Email String correo,
            @NotBlank String password) {}

    public record RefreshRequest(
            @NotBlank String refreshToken) {}

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tipo,
            long expiraEnSegundos,
            String nombre,
            String correo,
            String rol) {}
}