package com.altico.cakeserver.applications.ports.input.dto;

import java.time.LocalDateTime;

public record ImagenDto(
        Integer id,
        String url,
        Integer tortaId,
        LocalDateTime fechaCreado,
        LocalDateTime fechaActualizado
) {}
