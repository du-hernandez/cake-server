package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Actualizar Rol
@Schema(description = "Solicitud para actualizar un rol")
public record UpdateRolRequest(
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[A-Z_]+$", message = "Solo letras mayúsculas y guiones bajos")
        @Schema(description = "Nuevo nombre del rol")
        String nombre,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        @Schema(description = "Nueva descripción del rol")
        String descripcion,

        @Min(value = 0, message = "La prioridad debe ser mayor o igual a 0")
        @Max(value = 1000, message = "La prioridad debe ser menor o igual a 1000")
        @Schema(description = "Nueva prioridad del rol")
        Integer prioridad
) {}
