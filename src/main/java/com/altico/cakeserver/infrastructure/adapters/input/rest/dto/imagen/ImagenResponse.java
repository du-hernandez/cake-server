package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ImagenResponse(
        Integer id,
        String url,
        Integer tortaId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaCreado,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaActualizado,
        Links _links
) {
    public record Links(
            String self,
            String torta
    ) {}
}
