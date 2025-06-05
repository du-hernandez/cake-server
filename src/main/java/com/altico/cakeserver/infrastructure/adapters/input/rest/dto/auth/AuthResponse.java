package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Respuesta de autenticación")
public record AuthResponse(
        @Schema(description = "Token de acceso JWT")
        String accessToken,

        @Schema(description = "Token de refresco JWT")
        String refreshToken,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tokenType,

        @Schema(description = "Tiempo de expiración en segundos", example = "86400")
        long expiresIn,

        @Schema(description = "Información del usuario")
        UserInfo user
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}