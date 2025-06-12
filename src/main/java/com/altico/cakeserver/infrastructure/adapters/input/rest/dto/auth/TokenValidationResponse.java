package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

public record TokenValidationResponse(
        boolean valido,
        String mensaje
) {}
