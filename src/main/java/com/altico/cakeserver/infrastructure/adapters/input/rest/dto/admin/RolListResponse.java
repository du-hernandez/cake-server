package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// Lista de Roles
@Schema(description = "Lista paginada de roles")
public record RolListResponse(
        List<RolResponse> content,
        PageMetadata page
) {
    public record PageMetadata(
            int size,
            long totalElements,
            int totalPages,
            int number
    ) {}
}

