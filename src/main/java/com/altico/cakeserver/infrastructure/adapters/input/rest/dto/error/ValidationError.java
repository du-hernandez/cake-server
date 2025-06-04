package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error de validación de campo")
public record ValidationError(
        @Schema(description = "Nombre del campo con error", example = "descripcion")
        String field,

        @Schema(description = "Valor rechazado", example = "ab")
        Object rejectedValue,

        @Schema(description = "Mensaje de error", example = "La descripción debe tener entre 3 y 255 caracteres")
        String message
) {}
