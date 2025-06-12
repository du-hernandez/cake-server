package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// Lista de Usuarios
@Schema(description = "Lista paginada de usuarios")
public record AdminUserListResponse(
        List<AdminUserResponse> content,
        PageMetadata page
) {
    public record PageMetadata(
            int size,
            long totalElements,
            int totalPages,
            int number
    ) {}
}
