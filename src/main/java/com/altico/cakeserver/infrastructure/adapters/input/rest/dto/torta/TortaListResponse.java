package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import java.util.List;

public record TortaListResponse(
        List<TortaSummaryResponse> content,
        PageMetadata page
) {
    public record PageMetadata(
            int size,
            int totalElements,
            int totalPages,
            int number
    ) {}
}