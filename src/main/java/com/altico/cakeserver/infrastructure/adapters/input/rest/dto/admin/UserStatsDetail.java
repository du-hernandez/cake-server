package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// Detalle de Estadísticas por Usuario
@Schema(description = "Estadísticas detalladas por usuario")
public record UserStatsDetail(
        int totalSesiones,
        int sesionesActivas,
        int totalLoginsFallidos,
        LocalDateTime ultimoLoginExitoso,
        LocalDateTime ultimoLoginFallido,
        String dispositivoMasUsado
) {}
