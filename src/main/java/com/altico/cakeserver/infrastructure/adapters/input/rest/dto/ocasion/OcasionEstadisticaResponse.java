package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion;

public record OcasionEstadisticaResponse(
        Integer id,
        String nombre,
        Long cantidadTortas,
        boolean activo
) {}