package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record OcasionResponse(
        Integer id,
        String nombre,
        boolean activo,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaCreado,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaActualizado,
        Links _links
) {
    public record Links(
            String self,
            String tortas
    ) {}
}