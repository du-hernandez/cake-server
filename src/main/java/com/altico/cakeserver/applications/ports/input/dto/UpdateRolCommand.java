package com.altico.cakeserver.applications.ports.input.dto;

public record UpdateRolCommand(
        String nombre,
        String descripcion,
        Integer prioridad
) {}
