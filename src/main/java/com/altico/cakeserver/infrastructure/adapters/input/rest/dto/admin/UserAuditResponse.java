package com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

// Auditoría de Usuario
@Schema(description = "Registro de auditoría de usuario")
public record UserAuditResponse(
        Long id,
        String accion,
        String descripcion,
        String ipAddress,
        String userAgent,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha,
        String resultado
) {}
