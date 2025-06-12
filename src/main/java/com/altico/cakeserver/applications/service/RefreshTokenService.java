package com.altico.cakeserver.applications.service;

import com.altico.cakeserver.applications.ports.input.RefreshTokenServicePort;
import com.altico.cakeserver.applications.ports.output.RefreshTokenPersistencePort;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements RefreshTokenServicePort {

    private final RefreshTokenPersistencePort refreshTokenPersistence;

    // Configuración por defecto
    private long tiempoExpiracionMinutos = 10080; // 7 días
    private int limiteTokensPorUsuario = 5;
    private boolean limpiezaAutomaticaHabilitada = true;

    @Override
    public RefreshToken crearToken(String username, String deviceInfo, String ipAddress, String userAgent) {
        log.info("Creando refresh token para usuario: {}", username);

        // Verificar límite de tokens por usuario
        int tokensActivos = refreshTokenPersistence.countActiveByUsername(username);
        if (tokensActivos >= limiteTokensPorUsuario) {
            // Revocar el token más antiguo
            List<RefreshToken> tokens = refreshTokenPersistence.findActiveByUsername(username);
            if (!tokens.isEmpty()) {
                RefreshToken tokenMasAntiguo = tokens.stream()
                        .min((t1, t2) -> t1.fechaCreacion().compareTo(t2.fechaCreacion()))
                        .orElseThrow();
                revocarToken(tokenMasAntiguo.id());
            }
        }

        // Crear nuevo token
        String tokenId = UUID.randomUUID().toString();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expiracion = ahora.plusMinutes(tiempoExpiracionMinutos);

        RefreshToken token = new RefreshToken(
                tokenId,
                username,
                deviceInfo,
                ipAddress,
                userAgent,
                true,
                ahora,
                expiracion,
                ahora
        );

        RefreshToken tokenGuardado = refreshTokenPersistence.save(token);
        log.info("Refresh token creado exitosamente: {}", tokenId);

        return tokenGuardado;
    }

    @Override
    public RefreshToken renovarToken(String tokenId) {
        log.info("Renovando refresh token: {}", tokenId);

        RefreshToken token = obtenerTokenPorId(tokenId);

        // Validar que el token esté activo
        if (!token.activo()) {
            throw new RevokedRefreshTokenException(tokenId);
        }

        // Validar que no esté expirado
        if (LocalDateTime.now().isAfter(token.fechaExpiracion())) {
            throw new ExpiredRefreshTokenException(tokenId);
        }

        // Crear nuevo token
        RefreshToken nuevoToken = crearToken(
                token.username(),
                token.deviceInfo(),
                token.ipAddress(),
                token.userAgent()
        );

        // Revocar el token anterior
        revocarToken(tokenId);

        log.info("Token renovado exitosamente. Nuevo ID: {}", nuevoToken.id());
        return nuevoToken;
    }

    @Override
    public void revocarToken(String tokenId) {
        log.info("Revocando refresh token: {}", tokenId);

        if (!refreshTokenPersistence.existsById(tokenId)) {
            throw new RefreshTokenNotFoundException(tokenId);
        }

        refreshTokenPersistence.revokeById(tokenId);
        log.info("Token revocado exitosamente: {}", tokenId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esTokenValido(String tokenId) {
        try {
            RefreshToken token = obtenerTokenPorId(tokenId);

            // Verificar que esté activo
            if (!token.activo()) {
                return false;
            }

            // Verificar que no esté expirado
            if (LocalDateTime.now().isAfter(token.fechaExpiracion())) {
                return false;
            }

            // Registrar el uso del token
            registrarUsoToken(tokenId);

            return true;
        } catch (Exception e) {
            log.warn("Token inválido: {}", tokenId);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> obtenerTokensActivosPorUsuario(String username) {
        return refreshTokenPersistence.findActiveByUsername(username);
    }

    @Override
    public int revocarTodosLosTokensDelUsuario(String username) {
        log.info("Revocando todos los tokens del usuario: {}", username);
        return refreshTokenPersistence.revokeAllByUsername(username);
    }

    @Override
    public void revocarTokenPorUsuario(String tokenId, String username) {
        log.info("Usuario {} revocando token: {}", username, tokenId);

        RefreshToken token = obtenerTokenPorId(tokenId);

        // Verificar que el token pertenece al usuario
        if (!token.username().equals(username)) {
            throw new SecurityViolationException("El token no pertenece al usuario");
        }

        revocarToken(tokenId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefreshToken> listarTokens(Pageable pageable, String username) {
        return refreshTokenPersistence.findAllWithFilters(pageable, username, null);
    }

    @Override
    public int limpiarTokensExpirados() {
        log.info("Limpiando tokens expirados");
        int eliminados = refreshTokenPersistence.deleteExpired();
        log.info("Eliminados {} tokens expirados", eliminados);
        return eliminados;
    }

    @Override
    public int invalidarDispositivo(String deviceId) {
        log.info("Invalidando dispositivo: {}", deviceId);
        return refreshTokenPersistence.revokeAllByDeviceInfo(deviceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoSospechoso> obtenerDispositivosSospechosos() {
        return refreshTokenPersistence.findSuspiciousDevices();
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken obtenerTokenPorId(String tokenId) {
        return refreshTokenPersistence.findById(tokenId)
                .orElseThrow(() -> new RefreshTokenNotFoundException(tokenId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTokenParaUsuario(String username, String deviceId) {
        return refreshTokenPersistence.existsByUsernameAndDeviceInfo(username, deviceId);
    }

    @Override
    @Transactional(readOnly = true)
    public int contarTokensActivosPorUsuario(String username) {
        return refreshTokenPersistence.countActiveByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerDispositivosDelUsuario(String username) {
        return refreshTokenPersistence.findDevicesByUsername(username);
    }

    @Override
    public void registrarUsoToken(String tokenId) {
        refreshTokenPersistence.updateUltimoUso(tokenId, LocalDateTime.now());
    }

    @Override
    public void registrarIntentoCofcometoTokenInvalido(String tokenId, String ip) {
        log.warn("Intento de uso de token inválido: {} desde IP: {}", tokenId, ip);
        // Aquí podrías implementar lógica adicional de seguridad
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntentoTokenSospechoso> obtenerIntentosTokensSospechosos() {
        // Implementación básica - podrías expandir con una tabla específica
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public TokenEstadisticas obtenerEstadisticas() {
        return refreshTokenPersistence.getTokenEstadisticas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> obtenerTokensPorExpirar(int horas) {
        LocalDateTime limite = LocalDateTime.now().plusHours(horas);
        return refreshTokenPersistence.findExpiringBefore(limite);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> obtenerEstadisticasPorDispositivo() {
        return refreshTokenPersistence.getDeviceStatistics();
    }

    @Override
    public void configurarTiempoExpiracion(long minutos) {
        log.info("Configurando tiempo de expiración a {} minutos", minutos);
        this.tiempoExpiracionMinutos = minutos;
    }

    @Override
    public void configurarLimiteTokensPorUsuario(int limite) {
        log.info("Configurando límite de tokens por usuario a {}", limite);
        this.limiteTokensPorUsuario = limite;
    }

    @Override
    public void configurarLimpiezaAutomatica(boolean habilitada) {
        log.info("Configurando limpieza automática: {}", habilitada);
        this.limpiezaAutomaticaHabilitada = habilitada;
    }
}