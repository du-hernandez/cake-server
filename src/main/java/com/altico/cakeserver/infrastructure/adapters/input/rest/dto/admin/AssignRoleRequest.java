package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

// Asignar Rol
@Schema(description = "Solicitud para asignar un rol")
public record AssignRoleRequest(
        @NotBlank(message = "El rol es obligatorio")
        @Schema(description = "Nombre del rol", example = "ROLE_USER")
        String rol
) {}
