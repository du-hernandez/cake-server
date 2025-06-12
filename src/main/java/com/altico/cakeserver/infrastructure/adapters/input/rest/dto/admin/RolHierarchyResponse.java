package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

// Jerarquía de Roles
@Schema(description = "Jerarquía de roles ordenada por prioridad")
public record RolHierarchyResponse(
        @Schema(description = "ID del rol")
        Integer id,

        @Schema(description = "Nombre del rol")
        String nombre,

        @Schema(description = "Descripción del rol")
        String descripcion,

        @Schema(description = "Prioridad del rol")
        int prioridad,

        @Schema(description = "Nivel en la jerarquía")
        int nivel,

        @Schema(description = "Estado activo")
        boolean activo,

        @Schema(description = "Cantidad de usuarios")
        int cantidadUsuarios,

        @Schema(description = "Permisos únicos de este rol")
        Set<String> permisosUnicos
) {}
