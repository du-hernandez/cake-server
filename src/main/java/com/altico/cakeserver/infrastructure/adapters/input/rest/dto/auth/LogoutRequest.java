package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de cierre de sesión")
public record LogoutRequest(
        @NotBlank(message = "El token de refresco es obligatorio")
        @Schema(description = "Token de refresco a revocar")
        String refreshToken
) {}
