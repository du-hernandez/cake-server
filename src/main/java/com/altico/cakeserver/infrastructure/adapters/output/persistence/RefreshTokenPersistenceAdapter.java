package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.RefreshTokenPersistencePort;
import com.altico.cakeserver.domain.model.DispositivoSospechoso;
import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.domain.model.TokenEstadisticas;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RefreshTokenEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.RefreshTokenPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para RefreshToken siguiendo arquitectura hexagonal
 * Implementa el puerto de salida definido en la capa de aplicación
 */
@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenPersistenceMapper mapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        log.debug("Guardando refresh token: {}", token.id());

        try {
            RefreshTokenEntity entity = mapper.toEntity(token);
            RefreshTokenEntity saved = refreshTokenRepository.save(entity);
            RefreshToken result = mapper.toDomain(saved);

            log.debug("Token guardado exitosamente: {}", result.id());
            return result;
        } catch (Exception e) {
            log.error("Error guardando refresh token {}: {}", token.id(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findById(String id) {
        log.debug("Buscando refresh token por ID: {}", id);
        return refreshTokenRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        log.debug("Eliminando refresh token: {}", id);
        refreshTokenRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return refreshTokenRepository.existsById(id);
    }

    // ============== BÚSQUEDAS POR USUARIO ==============

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findByUsername(String username) {
        log.debug("Buscando tokens por username: {}", username);
        return refreshTokenRepository.findByUsername(username).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findActiveByUsername(String username) {
        return refreshTokenRepository.findByUsernameAndActivo(username, true).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int countActiveByUsername(String username) {
        return refreshTokenRepository.countByUsernameAndActivo(username, true);
    }

    // ============== BÚSQUEDAS POR DISPOSITIVO ==============

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findByDeviceInfo(String deviceInfo) {
        return refreshTokenRepository.findByDeviceInfo(deviceInfo).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameAndDeviceInfo(String username, String deviceInfo) {
        return refreshTokenRepository.existsByUsernameAndDeviceInfo(username, deviceInfo);
    }

    // ============== BÚSQUEDAS CON FILTROS ==============

    @Override
    @Transactional(readOnly = true)
    public Page<RefreshToken> findAllWithFilters(Pageable pageable, String username, Boolean activo) {
        log.debug("Buscando tokens con filtros - username: {}, activo: {}", username, activo);
        return refreshTokenRepository.findAllWithFilters(username, activo, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findByActivo(boolean activo) {
        return refreshTokenRepository.findByActivo(activo).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    // ============== OPERACIONES DE EXPIRACIÓN ==============

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findExpired() {
        return refreshTokenRepository.findExpired().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findExpiringBefore(LocalDateTime fecha) {
        return refreshTokenRepository.findByFechaExpiracionBefore(fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteExpired() {
        log.info("Eliminando tokens expirados");
        int deleted = refreshTokenRepository.deleteExpired();
        log.info("Eliminados {} tokens expirados", deleted);
        return deleted;
    }

    @Override
    public int deleteExpiredBefore(LocalDateTime fecha) {
        log.info("Eliminando tokens expirados antes de: {}", fecha);
        return refreshTokenRepository.deleteByFechaExpiracionBefore(fecha);
    }

    // ============== OPERACIONES DE REVOCACIÓN ==============

    @Override
    public int revokeAllByUsername(String username) {
        log.info("Revocando todos los tokens del usuario: {}", username);
        int revoked = refreshTokenRepository.revokeAllByUsername(username);
        log.info("Revocados {} tokens para usuario: {}", revoked, username);
        return revoked;
    }

    @Override
    public int revokeAllByDeviceInfo(String deviceInfo) {
        log.info("Revocando todos los tokens del dispositivo: {}", deviceInfo);
        return refreshTokenRepository.revokeAllByDeviceInfo(deviceInfo);
    }

    @Override
    public void revokeById(String id) {
        log.info("Revocando token: {}", id);
        refreshTokenRepository.revokeById(id);
    }

    // ============== SEGURIDAD Y DETECCIÓN DE ANOMALÍAS ==============

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoSospechoso> findSuspiciousDevices() {
        log.debug("Buscando dispositivos sospechosos");

        List<Object[]> results = refreshTokenRepository.findSuspiciousDevices();
        return results.stream()
                .map(row -> new DispositivoSospechoso(
                        (String) row[0], // deviceId
                        (String) row[1], // deviceInfo
                        (String) row[2], // ultimaIp
                        ((Number) row[3]).intValue(), // usuariosDiferentes
                        ((Number) row[4]).intValue(), // loginsFallidos
                        (String) row[5], // razonSospecha
                        (LocalDateTime) row[6], // primeraActividad
                        (LocalDateTime) row[7]  // ultimaActividad
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findByIpAddress(String ipAddress) {
        return refreshTokenRepository.findByIpAddress(ipAddress).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findDevicesByUsername(String username) {
        return refreshTokenRepository.findDistinctDeviceInfoByUsername(username);
    }

    // ============== ESTADÍSTICAS ==============

    @Override
    @Transactional(readOnly = true)
    public TokenEstadisticas getTokenEstadisticas() {
        log.debug("Obteniendo estadísticas de tokens");

        LocalDateTime ahora24h = LocalDateTime.now().plusHours(24);
        Object[] stats = refreshTokenRepository.getTokenEstadisticas(ahora24h);

        return new TokenEstadisticas(
                ((Number) stats[0]).longValue(), // totalTokens
                ((Number) stats[1]).longValue(), // tokensActivos
                ((Number) stats[2]).longValue(), // tokensExpirados
                ((Number) stats[3]).longValue(), // tokensRevocados
                ((Number) stats[4]).longValue(), // tokensPorExpirar24h
                ((Number) stats[5]).longValue(), // sesionesUnicas
                ((Number) stats[6]).longValue(), // dispositivosUnicos
                ((Number) stats[7]).doubleValue() // promedioSesionesPorUsuario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getDeviceStatistics() {
        List<Object[]> results = refreshTokenRepository.getDeviceStatistics();
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin) {
        return refreshTokenRepository.countByFechaCreacionBetween(inicio, fin);
    }

    // ============== MANTENIMIENTO ==============

    @Override
    public void updateUltimoUso(String tokenId, LocalDateTime ultimoUso) {
        log.debug("Actualizando último uso para token: {}", tokenId);
        refreshTokenRepository.updateUltimoUso(tokenId, ultimoUso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findInactiveOlderThan(LocalDateTime fecha) {
        return refreshTokenRepository.findInactiveOlderThan(fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}