package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

// Reset Password
@Schema(description = "Respuesta de reseteo de contraseña")
public record ResetPasswordResponse(
        String mensaje,
        String nuevaPasswordTemporal,
        String instrucciones
) {}