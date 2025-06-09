package com.altico.cakeserver.applications.ports.input.dto;

public record UpdatePermisoCommand(
        String nombre,
        String descripcion
) {}
