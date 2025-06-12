package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

// Estadísticas de Usuario
@Schema(description = "Estadísticas de usuarios del sistema")
public record UserStatsResponse(
        long totalUsuarios,
        long usuariosActivos,
        long usuariosInactivos,
        long usuariosSinRoles,
        long usuariosConMultiplesRoles,
        long ultimasHoras24,
        long ultimosDias7,
        long ultimosDias30
) {}
