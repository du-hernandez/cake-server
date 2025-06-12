package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import jakarta.validation.Valid;

public record VerifyTokenRequest(
        @Valid String token
) {}
