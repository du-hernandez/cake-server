package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Información de dispositivo del usuario")
public record DeviceInfoResponse(
        @Schema(description = "Identificador único del dispositivo")
        String deviceId,

        @Schema(description = "Información del dispositivo")
        String deviceInfo,

        @Schema(description = "Dirección IP")
        String ipAddress,

        @Schema(description = "User Agent")
        String userAgent,

        @Schema(description = "Estado activo")
        boolean activo,

        @Schema(description = "Fecha de primera conexión")
        LocalDateTime primeraConexion,

        @Schema(description = "Última actividad")
        LocalDateTime ultimaActividad,

        @Schema(description = "Cantidad de tokens activos")
        int tokensActivos
) {}
