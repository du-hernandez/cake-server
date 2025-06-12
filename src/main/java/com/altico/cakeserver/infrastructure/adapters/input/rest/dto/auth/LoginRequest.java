package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de inicio de sesión")
public record LoginRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Schema(description = "Nombre de usuario", example = "admin")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña", example = "password123")
        String password
) {}