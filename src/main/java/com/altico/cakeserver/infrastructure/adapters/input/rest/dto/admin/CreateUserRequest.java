package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

// Crear Usuario
@Schema(description = "Solicitud para crear un nuevo usuario")
public record CreateUserRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Solo letras, números y guiones bajos")
        @Schema(description = "Nombre de usuario único", example = "nuevo_usuario")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email debe ser válido")
        @Schema(description = "Correo electrónico", example = "usuario@ejemplo.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Schema(description = "Contraseña", example = "password123")
        String password,

        @Schema(description = "Estado activo del usuario", example = "true")
        boolean activo,

        @Schema(description = "Roles a asignar al usuario")
        Set<String> roles
) {}
