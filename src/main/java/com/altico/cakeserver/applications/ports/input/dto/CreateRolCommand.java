package com.altico.cakeserver.applications.ports.input.dto;

import java.util.Set;

// Comandos para Rol
public record CreateRolCommand(
        String nombre,
        String descripcion,
        int prioridad,
        Set<Integer> permisoIds
) {}
