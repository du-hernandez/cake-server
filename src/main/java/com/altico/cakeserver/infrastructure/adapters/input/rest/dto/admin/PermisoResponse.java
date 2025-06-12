package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// Respuesta de Permiso
@Schema(description = "Respuesta con información de permiso")
public record PermisoResponse(
        @Schema(description = "ID del permiso")
        Integer id,

        @Schema(description = "Nombre del permiso")
        String nombre,

        @Schema(description = "Descripción del permiso")
        String descripcion,

        @Schema(description = "Recurso del sistema")
        String recurso,

        @Schema(description = "Acción permitida")
        String accion,

        @Schema(description = "Código completo del permiso")
        String codigoCompleto,

        @Schema(description = "Estado activo")
        boolean activo,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Fecha de creación")
        LocalDateTime fechaCreado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Última actualización")
        LocalDateTime fechaActualizado
) {}
