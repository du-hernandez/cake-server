package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;

// Intento de Token Sospechoso
public record IntentoTokenSospechoso(
        String tokenId,
        String ip,
        String userAgent,
        LocalDateTime fecha,
        String tipoIntento
) {}
