package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.domain.model.TokenEstadisticas;
import com.altico.cakeserver.domain.model.DispositivoSospechoso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Refresh Tokens
 */
public interface RefreshTokenPersistencePort {

    // Operaciones CRUD básicas
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findById(String id);
    void deleteById(String id);
    boolean existsById(String id);

    // Búsquedas por usuario
    List<RefreshToken> findByUsername(String username);
    List<RefreshToken> findActiveByUsername(String username);
    int countActiveByUsername(String username);

    // Búsquedas por dispositivo
    List<RefreshToken> findByDeviceInfo(String deviceInfo);
    boolean existsByUsernameAndDeviceInfo(String username, String deviceInfo);

    // Búsquedas con filtros
    Page<RefreshToken> findAllWithFilters(Pageable pageable, String username, Boolean activo);
    List<RefreshToken> findByActivo(boolean activo);

    // Operaciones de expiración
    List<RefreshToken> findExpired();
    List<RefreshToken> findExpiringBefore(LocalDateTime fecha);
    int deleteExpired();
    int deleteExpiredBefore(LocalDateTime fecha);

    // Operaciones de revocación
    int revokeAllByUsername(String username);
    int revokeAllByDeviceInfo(String deviceInfo);
    void revokeById(String id);

    // Seguridad y detección de anomalías
    List<DispositivoSospechoso> findSuspiciousDevices();
    List<RefreshToken> findByIpAddress(String ipAddress);
    List<String> findDevicesByUsername(String username);

    // Estadísticas
    TokenEstadisticas getTokenEstadisticas();
    Map<String, Integer> getDeviceStatistics();
    long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    // Mantenimiento
    void updateUltimoUso(String tokenId, LocalDateTime ultimoUso);
    List<RefreshToken> findInactiveOlderThan(LocalDateTime fecha);
}
