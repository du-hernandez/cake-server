package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta de cierre de sesión")
public record LogoutResponse(
        @Schema(description = "Mensaje de confirmación")
        String mensaje,

        @Schema(description = "Timestamp del logout")
        LocalDateTime timestamp
) {}
