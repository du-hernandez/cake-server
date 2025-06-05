package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Información del usuario autenticado")
public record UserInfo(
        @Schema(description = "ID del usuario")
        Long id,

        @Schema(description = "Nombre de usuario")
        String username,

        @Schema(description = "Correo electrónico")
        String email,

        @Schema(description = "Roles del usuario")
        Set<String> roles,

        @Schema(description = "Estado activo del usuario")
        boolean activo
) {}
