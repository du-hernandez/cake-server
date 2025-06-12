package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

// Actualizar Permiso
@Schema(description = "Solicitud para actualizar un permiso")
public record UpdatePermisoRequest(
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        @Schema(description = "Nuevo nombre del permiso")
        String nombre,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        @Schema(description = "Nueva descripción del permiso")
        String descripcion
) {}
