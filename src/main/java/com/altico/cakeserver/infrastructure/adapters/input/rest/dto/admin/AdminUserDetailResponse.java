package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

// Detalle Completo de Usuario
@Schema(description = "Respuesta detallada de usuario con roles y permisos")
public record AdminUserDetailResponse(
        @Schema(description = "ID del usuario")
        Long id,

        @Schema(description = "Nombre de usuario")
        String username,

        @Schema(description = "Correo electrónico")
        String email,

        @Schema(description = "Estado activo")
        boolean activo,

        @Schema(description = "Roles completos asignados")
        Set<RolResponse> roles,

        @Schema(description = "Todos los permisos efectivos")
        Set<String> permisosEfectivos,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaCreado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaActualizado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime ultimoAcceso,

        @Schema(description = "Estadísticas del usuario")
        UserStatsDetail estadisticas
) {}
