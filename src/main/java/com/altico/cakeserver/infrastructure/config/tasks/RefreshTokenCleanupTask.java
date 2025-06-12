package com.altico.cakeserver.infrastructure.config.tasks;

import com.altico.cakeserver.applications.ports.input.RefreshTokenServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada para la limpieza automática de tokens expirados
 * Se ejecuta cada hora por defecto
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "app.refresh-token.cleanup.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RefreshTokenCleanupTask {

    private final RefreshTokenServicePort refreshTokenService;

    /**
     * Limpia tokens expirados cada hora
     * Cron: segundo minuto hora día mes díaSemana
     * 0 0 * * * * = cada hora en punto
     */
    @Scheduled(cron = "${app.refresh-token.cleanup.cron:0 0 * * * *}")
    public void limpiarTokensExpirados() {
        log.info("Iniciando limpieza programada de refresh tokens expirados");

        try {
            int tokensEliminados = refreshTokenService.limpiarTokensExpirados();

            if (tokensEliminados > 0) {
                log.info("Limpieza completada: {} tokens expirados eliminados", tokensEliminados);
            } else {
                log.debug("Limpieza completada: no hay tokens expirados para eliminar");
            }

        } catch (Exception e) {
            log.error("Error durante la limpieza de tokens expirados: {}", e.getMessage(), e);
        }
    }

    /**
     * Reporta estadísticas de tokens cada 6 horas
     * Útil para monitoreo y alertas
     */
    @Scheduled(cron = "${app.refresh-token.stats.cron:0 0 */6 * * *}")
    public void reportarEstadisticas() {
        try {
            var stats = refreshTokenService.obtenerEstadisticas();

            log.info("📊 Estadísticas de Refresh Tokens:");
            log.info("   • Total: {}", stats.totalTokens());
            log.info("   • Activos: {}", stats.tokensActivos());
            log.info("   • Expirados: {}", stats.tokensExpirados());
            log.info("   • Revocados: {}", stats.tokensRevocados());
            log.info("   • Por expirar (24h): {}", stats.tokensPorExpirar24h());
            log.info("   • Sesiones únicas: {}", stats.sesionesUnicas());
            log.info("   • Dispositivos únicos: {}", stats.dispositivosUnicos());
            log.info("   • Promedio sesiones/usuario: {:.2f}", stats.promedioSesionesPorUsuario());

            // Alerta si hay muchos tokens por expirar
            if (stats.tokensPorExpirar24h() > 100) {
                log.warn("⚠️ ALERTA: {} tokens expirarán en las próximas 24 horas",
                        stats.tokensPorExpirar24h());
            }

            // Alerta si hay muchos dispositivos sospechosos
            var dispositivosSospechosos = refreshTokenService.obtenerDispositivosSospechosos();
            if (!dispositivosSospechosos.isEmpty()) {
                log.warn("🔒 SEGURIDAD: {} dispositivos marcados como sospechosos",
                        dispositivosSospechosos.size());
            }

        } catch (Exception e) {
            log.error("Error al generar estadísticas de tokens: {}", e.getMessage());
        }
    }

    /**
     * Limpieza profunda semanal - elimina tokens inactivos antiguos
     * Se ejecuta los domingos a las 2:00 AM
     */
    @Scheduled(cron = "${app.refresh-token.deep-cleanup.cron:0 0 2 * * SUN}")
    public void limpiezaProfunda() {
        log.info("Iniciando limpieza profunda semanal de refresh tokens");

        try {
            // Eliminar tokens inactivos de más de 30 días
            var fechaLimite = java.time.LocalDateTime.now().minusDays(30);
            var tokensInactivos = refreshTokenService
                    .obtenerTokensPorExpirar(24 * 30); // 30 días en horas

            int eliminados = 0;
            for (var token : tokensInactivos) {
                if (!token.activo() && token.ultimoUso() != null &&
                        token.ultimoUso().isBefore(fechaLimite)) {
                    refreshTokenService.revocarToken(token.id());
                    eliminados++;
                }
            }

            log.info("Limpieza profunda completada: {} tokens inactivos antiguos eliminados", eliminados);

        } catch (Exception e) {
            log.error("Error durante la limpieza profunda: {}", e.getMessage(), e);
        }
    }

    /**
     * Detecta y reporta actividad sospechosa diariamente
     * Se ejecuta a las 8:00 AM todos los días
     */
    @Scheduled(cron = "${app.refresh-token.security-check.cron:0 0 8 * * *}")
    public void verificarSeguridadDiaria() {
        log.info("Verificando actividad sospechosa en refresh tokens");

        try {
            var dispositivosSospechosos = refreshTokenService.obtenerDispositivosSospechosos();

            if (!dispositivosSospechosos.isEmpty()) {
                log.warn("🚨 ALERTA DE SEGURIDAD: Detectados {} dispositivos sospechosos:",
                        dispositivosSospechosos.size());

                for (var dispositivo : dispositivosSospechosos) {
                    log.warn("   • Dispositivo: {} | IP: {} | Usuarios: {} | Razón: {}",
                            dispositivo.deviceInfo(),
                            dispositivo.ultimaIp(),
                            dispositivo.usuariosDiferentes(),
                            dispositivo.razonSospecha());
                }

                // Aquí podrías enviar alertas por email o notificaciones
                // notificationService.enviarAlertaSeguridad(dispositivosSospechosos);
            }

        } catch (Exception e) {
            log.error("Error durante la verificación de seguridad: {}", e.getMessage());
        }
    }
}