package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

// Cambiar Estado de Permiso
@Schema(description = "Solicitud para cambiar el estado de un permiso")
public record ChangePermisoStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        @Schema(description = "Nuevo estado del permiso", example = "true")
        Boolean activo
) {}
