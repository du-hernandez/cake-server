package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TortaSummaryResponse(
        Integer id,
        String descripcion,
        String imagenPrincipal,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaCreado,
        int cantidadOcasiones,
        int cantidadImagenes,
        String _link
) {}