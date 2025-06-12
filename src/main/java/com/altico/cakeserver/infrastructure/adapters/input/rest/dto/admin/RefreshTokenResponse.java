package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// Respuesta de Token
@Schema(description = "Información de un refresh token")
public record RefreshTokenResponse(
        @Schema(description = "ID del token")
        String id,

        @Schema(description = "Usuario propietario")
        String username,

        @Schema(description = "Información del dispositivo")
        String deviceInfo,

        @Schema(description = "Dirección IP")
        String ipAddress,

        @Schema(description = "User Agent")
        String userAgent,

        @Schema(description = "Estado del token")
        boolean activo,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Fecha de creación")
        LocalDateTime fechaCreacion,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Fecha de expiración")
        LocalDateTime fechaExpiracion,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Último uso")
        LocalDateTime ultimoUso
) {}
