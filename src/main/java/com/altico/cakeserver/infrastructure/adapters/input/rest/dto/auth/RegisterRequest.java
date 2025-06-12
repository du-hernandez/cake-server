package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de registro de usuario")
public record RegisterRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
        @Schema(description = "Nombre de usuario", example = "nuevo_usuario")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        @Schema(description = "Correo electrónico", example = "usuario@example.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        @Schema(description = "Contraseña", example = "password123")
        String password
) {}