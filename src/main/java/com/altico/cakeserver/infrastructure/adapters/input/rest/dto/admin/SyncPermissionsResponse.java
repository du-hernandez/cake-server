package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

// Sincronización de Permisos
@Schema(description = "Resultado de la sincronización de permisos del sistema")
public record SyncPermissionsResponse(
        @Schema(description = "Mensaje del resultado")
        String mensaje,

        @Schema(description = "Permisos creados")
        int permisosCreados,

        @Schema(description = "Permisos actualizados")
        int permisosActualizados,

        @Schema(description = "Permisos desactivados")
        int permisosDesactivados,

        @Schema(description = "Nuevos recursos detectados")
        List<String> nuevosRecursos,

        @Schema(description = "Nuevas acciones detectadas")
        List<String> nuevasAcciones,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Fecha de sincronización")
        LocalDateTime fechaSincronizacion
) {}
