package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen;

import java.util.List;

public record ImagenListResponse(
        List<ImagenResponse> imagenes,
        int total
) {}
