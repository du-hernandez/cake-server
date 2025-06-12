package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;

// Dispositivo Sospechoso
public record DispositivoSospechoso(
        String deviceId,
        String deviceInfo,
        String ultimaIp,
        int usuariosDiferentes,
        int loginsFallidos,
        String razonSospecha,
        LocalDateTime primeraActividad,
        LocalDateTime ultimaActividad
) {}