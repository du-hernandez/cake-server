package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de error estándar")
public record ErrorResponse(
        @Schema(description = "Código de estado HTTP", example = "400")
        int status,

        @Schema(description = "Mensaje de error", example = "Solicitud inválida")
        String error,

        @Schema(description = "Descripción detallada del error", example = "La descripción de la torta es obligatoria")
        String message,

        @Schema(description = "Ruta donde ocurrió el error", example = "/api/v1/tortas")
        String path,

        @Schema(description = "Momento en que ocurrió el error")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "Lista de errores de validación")
        List<ValidationError> validationErrors
) {
    // Constructor para errores simples
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, LocalDateTime.now(), null);
    }

    // Constructor para errores con validaciones
    public ErrorResponse(int status, String error, String message, String path, List<ValidationError> validationErrors) {
        this(status, error, message, path, LocalDateTime.now(), validationErrors);
    }
}
