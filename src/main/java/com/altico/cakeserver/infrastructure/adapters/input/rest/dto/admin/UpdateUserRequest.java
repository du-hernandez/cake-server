package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Actualizar Usuario
@Schema(description = "Solicitud para actualizar un usuario")
public record UpdateUserRequest(
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Solo letras, números y guiones bajos")
        @Schema(description = "Nuevo nombre de usuario")
        String username,

        @Email(message = "Email debe ser válido")
        @Schema(description = "Nuevo correo electrónico")
        String email,

        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Schema(description = "Nueva contraseña")
        String password
) {}
