package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

// Crear Rol
@Schema(description = "Solicitud para crear un rol")
public record CreateRolRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[A-Z_]+$", message = "Solo letras mayúsculas y guiones bajos")
        @Schema(description = "Nombre del rol", example = "ROLE_MANAGER")
        String nombre,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        @Schema(description = "Descripción del rol", example = "Rol para gerentes de área")
        String descripcion,

        @Min(value = 0, message = "La prioridad debe ser mayor o igual a 0")
        @Max(value = 1000, message = "La prioridad debe ser menor o igual a 1000")
        @Schema(description = "Prioridad del rol (menor número = mayor prioridad)", example = "100")
        int prioridad,

        @Schema(description = "IDs de permisos a asignar al rol")
        Set<Integer> permisoIds
) {}
