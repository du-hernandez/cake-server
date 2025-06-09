package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// Lista de Permisos
@Schema(description = "Lista paginada de permisos")
public record PermisoListResponse(
        List<PermisoResponse> content,
        PageMetadata page
) {
    public record PageMetadata(
            int size,
            long totalElements,
            int totalPages,
            int number
    ) {}
}
