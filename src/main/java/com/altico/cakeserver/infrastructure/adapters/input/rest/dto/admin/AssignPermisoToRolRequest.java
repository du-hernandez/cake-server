package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Asignar Permiso a Rol
@Schema(description = "Solicitud para asignar un permiso a un rol")
public record AssignPermisoToRolRequest(
        @NotNull(message = "El ID del permiso es obligatorio")
        @Positive(message = "El ID del permiso debe ser positivo")
        @Schema(description = "ID del permiso a asignar", example = "1")
        Integer permisoId
) {}
