package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

// Cambiar Estado de Rol
@Schema(description = "Solicitud para cambiar el estado de un rol")
public record ChangeRolStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        @Schema(description = "Nuevo estado del rol", example = "true")
        Boolean activo
) {}
