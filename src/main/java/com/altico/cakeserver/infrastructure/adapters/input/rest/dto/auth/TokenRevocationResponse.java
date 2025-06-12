package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

public record TokenRevocationResponse(
        String mensaje,
        int tokensRevocados,
        String objetivo
) {}
