package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// Dispositivo Sospechoso
@Schema(description = "Información de dispositivo con actividad sospechosa")
public record SuspiciousDeviceResponse(
        @Schema(description = "ID del dispositivo")
        String deviceId,

        @Schema(description = "Información del dispositivo")
        String deviceInfo,

        @Schema(description = "Última IP conocida")
        String ultimaIp,

        @Schema(description = "Cantidad de usuarios diferentes")
        int usuariosDiferentes,

        @Schema(description = "Intentos de login fallidos")
        int loginsFallidos,

        @Schema(description = "Razón de sospecha")
        String razonSospecha,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Primera actividad detectada")
        LocalDateTime primeraActividad,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Última actividad detectada")
        LocalDateTime ultimaActividad
) {}