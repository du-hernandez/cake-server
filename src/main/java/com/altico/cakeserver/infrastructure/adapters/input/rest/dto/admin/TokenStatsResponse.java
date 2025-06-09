package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

// Estadísticas de Tokens
@Schema(description = "Estadísticas de tokens del sistema")
public record TokenStatsResponse(
        long totalTokens,
        long tokensActivos,
        long tokensExpirados,
        long tokensRevocados,
        long tokensPorExpirar24h,
        long sesionesUnicas,
        long dispositivosUnicos,
        double promedioSesionesPorUsuario
) {}
