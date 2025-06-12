package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

public record TokenCleanupResponse(
        String mensaje,
        int tokensEliminados,
        String descripcion
) {}
