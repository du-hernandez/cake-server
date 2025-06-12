package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// Lista de Tokens
@Schema(description = "Lista paginada de refresh tokens")
public record RefreshTokenListResponse(
        List<RefreshTokenResponse> content,
        PageMetadata page
) {
    public record PageMetadata(
            int size,
            long totalElements,
            int totalPages,
            int number
    ) {}
}
