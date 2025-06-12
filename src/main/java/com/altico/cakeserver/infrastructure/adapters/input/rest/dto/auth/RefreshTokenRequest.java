package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de renovación de token")
public record RefreshTokenRequest(
        @NotBlank(message = "El token de refresco es obligatorio")
        @Schema(description = "Token de refresco JWT")
        String refreshToken
) {}
