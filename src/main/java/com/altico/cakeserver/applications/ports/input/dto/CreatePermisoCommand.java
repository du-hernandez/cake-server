package com.altico.cakeserver.applications.ports.input.dto;

// Comandos para Permiso
public record CreatePermisoCommand(
        String nombre,
        String descripcion,
        String recurso,
        String accion
) {}
