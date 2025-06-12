package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

// Respuesta de Usuario Admin
@Schema(description = "Respuesta con información de usuario para administración")
public record AdminUserResponse(
        @Schema(description = "ID del usuario")
        Long id,

        @Schema(description = "Nombre de usuario")
        String username,

        @Schema(description = "Correo electrónico")
        String email,

        @Schema(description = "Estado activo")
        boolean activo,

        @Schema(description = "Roles asignados")
        Set<String> roles,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Fecha de creación")
        LocalDateTime fechaCreado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Última actualización")
        LocalDateTime fechaActualizado,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Último acceso")
        LocalDateTime ultimoAcceso
) {}
