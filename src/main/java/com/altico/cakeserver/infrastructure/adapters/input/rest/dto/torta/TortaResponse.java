package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.OcasionResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen.ImagenResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Set;

public record TortaResponse(
        Integer id,
        String descripcion,
        String imagenPrincipal,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaCreado,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaActualizado,
        Set<OcasionResponse> ocasiones,
        Set<ImagenResponse> imagenes,
        Links _links
) {
    public record Links(
            String self,
            String ocasiones,
            String imagenes
    ) {}
}