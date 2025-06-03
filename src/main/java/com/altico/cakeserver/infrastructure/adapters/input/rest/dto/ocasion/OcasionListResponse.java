package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion;

import java.util.List;

public record OcasionListResponse(
        List<OcasionResponse> content,
        PageMetadata page
) {
    public record PageMetadata(
            int size,
            int totalElements,
            int totalPages,
            int number
    ) {}
}