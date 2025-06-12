package com.altico.cakeserver.infrastructure.config.tasks;

// ============== CONFIGURACIÓN ADICIONAL ==============

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Configuración para habilitar tareas programadas
 */
@org.springframework.context.annotation.Configuration
@org.springframework.scheduling.annotation.EnableScheduling
@ConditionalOnProperty(
        name = "app.refresh-token.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
class RefreshTokenSchedulingConfig {
    // Esta clase habilita el scheduling solo cuando está configurado
}