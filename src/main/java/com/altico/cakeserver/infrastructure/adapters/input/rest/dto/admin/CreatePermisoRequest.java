package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Crear Permiso
@Schema(description = "Solicitud para crear un permiso")
public record CreatePermisoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        @Schema(description = "Nombre del permiso", example = "Crear Torta")
        String nombre,

        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        @Schema(description = "Descripción del permiso", example = "Permite crear nuevas tortas")
        String descripcion,

        @NotBlank(message = "El recurso es obligatorio")
        @Size(max = 50, message = "El recurso no puede exceder 50 caracteres")
        @Schema(description = "Recurso del sistema", example = "tortas")
        String recurso,

        @NotBlank(message = "La acción es obligatoria")
        @Size(max = 50, message = "La acción no puede exceder 50 caracteres")
        @Schema(description = "Acción permitida", example = "create")
        String accion
) {}
