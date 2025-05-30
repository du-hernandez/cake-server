package com.altico.cakeserver.applications.ports.input.dto;

import java.time.LocalDateTime;

public record OcasionDto(
        Integer id,
        String nombre,
        boolean activo,
        LocalDateTime fechaCreado,
        LocalDateTime fechaActualizado
) {}

