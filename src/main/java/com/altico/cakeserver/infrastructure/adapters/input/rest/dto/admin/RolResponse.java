package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// Respuesta de Rol
@Schema(description = "Respuesta con información básica de rol")
public record RolResponse(
        @Schema(description = "ID del rol")
        Integer id,

        @Schema(description = "Nombre del rol")
        String nombre,

        @Schema(description = "Descripción del rol")
        String descripcion,

        @Schema(description = "Prioridad del rol")
        int prioridad,

        @Schema(description = "Estado activo")
        boolean activo,

        @Schema(description = "Cantidad de permisos asignados")
        int cantidadPermisos,

        @Schema(description = "Cantidad de usuarios con este rol")
        int cantidadUsuarios,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Fecha de creación")
        LocalDateTime fechaCreado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Última actualización")
        LocalDateTime fechaActualizado
) {}
