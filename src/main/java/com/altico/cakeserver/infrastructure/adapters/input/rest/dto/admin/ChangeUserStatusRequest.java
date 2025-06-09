package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

// Cambiar Estado
@Schema(description = "Solicitud para cambiar el estado de un usuario")
public record ChangeUserStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        @Schema(description = "Nuevo estado del usuario", example = "true")
        Boolean activo
) {}
