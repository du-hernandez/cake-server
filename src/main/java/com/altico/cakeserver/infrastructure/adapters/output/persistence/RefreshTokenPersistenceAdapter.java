package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.RefreshTokenPersistencePort;
import com.altico.cakeserver.domain.model.DispositivoSospechoso;
import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.domain.model.TokenEstadisticas;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RefreshTokenEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.AdminPersistenceMapper;
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

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AdminPersistenceMapper mapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        log.debug("Guardando refresh token: {}", token.id());
        RefreshTokenEntity entity = mapper.toEntity(token);
        RefreshTokenEntity saved = refreshTokenRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findById(String id) {
        return refreshTokenRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        refreshTokenRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return refreshTokenRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findByUsername(String username) {
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

    @Override
    @Transactional(readOnly = true)
    public Page<RefreshToken> findAllWithFilters(Pageable pageable, String username, Boolean activo) {
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
        return refreshTokenRepository.deleteExpired();
    }

    @Override
    public int deleteExpiredBefore(LocalDateTime fecha) {
        return refreshTokenRepository.deleteByFechaExpiracionBefore(fecha);
    }

    @Override
    public int revokeAllByUsername(String username) {
        return refreshTokenRepository.revokeAllByUsername(username);
    }

    @Override
    public int revokeAllByDeviceInfo(String deviceInfo) {
        return refreshTokenRepository.revokeAllByDeviceInfo(deviceInfo);
    }

    @Override
    public void revokeById(String id) {
        refreshTokenRepository.revokeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoSospechoso> findSuspiciousDevices() {
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

    @Override
    @Transactional(readOnly = true)
    public TokenEstadisticas getTokenEstadisticas() {
        // Calcula la fecha y hora de ahora más 24 horas
        LocalDateTime ahoraMas24Horas = LocalDateTime.now().plusDays(1);

        // Llama al método del repositorio pasando el parámetro calculado
        Object[] stats = refreshTokenRepository.getTokenEstadisticas(ahoraMas24Horas);

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

    @Override
    public void updateUltimoUso(String tokenId, LocalDateTime ultimoUso) {
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
