package com.museogestion.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class PerfilDto {

    public record PerfilRequest(
            @NotBlank String nombre,
            String telefono,
            String ciudad) {}

    public record PerfilResponse(
            String nombre,
            String correo,
            String telefono,
            String ciudad,
            String rol) {}
}