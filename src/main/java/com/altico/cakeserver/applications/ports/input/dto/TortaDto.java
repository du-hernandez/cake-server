package com.altico.cakeserver.applications.ports.input.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record TortaDto(
        Integer id,
        String descripcion,
        String imagen,
        LocalDateTime fechaCreado,
        LocalDateTime fechaActualizado,
        Set<OcasionDto> ocasiones,
        Set<ImagenDto> imagenes
) {}
