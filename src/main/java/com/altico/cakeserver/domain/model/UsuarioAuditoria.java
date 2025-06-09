package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;

// Auditoría de Usuario
public record UsuarioAuditoria(
        Long id,
        Long usuarioId,
        String accion,
        String descripcion,
        String ipAddress,
        String userAgent,
        LocalDateTime fecha,
        String resultado
) {}
