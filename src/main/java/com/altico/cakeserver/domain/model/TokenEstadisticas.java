package com.altico.cakeserver.domain.model;

// Estadísticas de Tokens
public record TokenEstadisticas(
        long totalTokens,
        long tokensActivos,
        long tokensExpirados,
        long tokensRevocados,
        long tokensPorExpirar24h,
        long sesionesUnicas,
        long dispositivosUnicos,
        double promedioSesionesPorUsuario
) {}
