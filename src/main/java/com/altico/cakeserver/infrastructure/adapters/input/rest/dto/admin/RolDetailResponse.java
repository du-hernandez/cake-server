package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

// Detalle Completo de Rol
@Schema(description = "Respuesta detallada de rol con todos sus permisos")
public record RolDetailResponse(
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

        @Schema(description = "Permisos asignados al rol")
        Set<PermisoResponse> permisos,

        @Schema(description = "Códigos completos de permisos")
        Set<String> codigosPermisos,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaCreado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaActualizado
) {}
