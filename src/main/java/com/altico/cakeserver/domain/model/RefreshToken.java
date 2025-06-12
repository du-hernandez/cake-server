package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;

// Refresh Token
public record RefreshToken(
        String id,
        String username,
        String deviceInfo,
        String ipAddress,
        String userAgent,
        boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaExpiracion,
        LocalDateTime ultimoUso
) {}
