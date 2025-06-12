package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RefreshTokenEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {

    // Búsquedas básicas por usuario
    List<RefreshTokenEntity> findByUsername(String username);
    List<RefreshTokenEntity> findByUsernameAndActivo(String username, boolean activo);
    int countByUsernameAndActivo(String username, boolean activo);

    // Búsquedas por dispositivo
    List<RefreshTokenEntity> findByDeviceInfo(String deviceInfo);
    boolean existsByUsernameAndDeviceInfo(String username, String deviceInfo);

    // Búsquedas por estado
    List<RefreshTokenEntity> findByActivo(boolean activo);

    // Búsquedas por IP
    List<RefreshTokenEntity> findByIpAddress(String ipAddress);

    // Búsquedas con filtros
    @Query("SELECT rt FROM RefreshTokenEntity rt " +
            "WHERE (:username IS NULL OR rt.username = :username) " +
            "AND (:activo IS NULL OR rt.activo = :activo) " +
            "ORDER BY rt.fechaCreacion DESC")
    Page<RefreshTokenEntity> findAllWithFilters(@Param("username") String username,
                                                @Param("activo") Boolean activo,
                                                Pageable pageable);

    // Tokens expirados
    @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.fechaExpiracion < CURRENT_TIMESTAMP")
    List<RefreshTokenEntity> findExpired();

    List<RefreshTokenEntity> findByFechaExpiracionBefore(LocalDateTime fecha);

    // Eliminar tokens expirados
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.fechaExpiracion < CURRENT_TIMESTAMP")
    int deleteExpired();

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.fechaExpiracion < :fecha")
    int deleteByFechaExpiracionBefore(@Param("fecha") LocalDateTime fecha);

    // Revocar tokens
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.activo = false WHERE rt.username = :username")
    int revokeAllByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.activo = false WHERE rt.deviceInfo = :deviceInfo")
    int revokeAllByDeviceInfo(@Param("deviceInfo") String deviceInfo);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.activo = false WHERE rt.id = :id")
    void revokeById(@Param("id") String id);

    // Actualizar último uso
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.ultimoUso = :ultimoUso WHERE rt.id = :id")
    void updateUltimoUso(@Param("id") String id, @Param("ultimoUso") LocalDateTime ultimoUso);

    // Dispositivos únicos por usuario
    @Query("SELECT DISTINCT rt.deviceInfo FROM RefreshTokenEntity rt WHERE rt.username = :username")
    List<String> findDistinctDeviceInfoByUsername(@Param("username") String username);

    // Contadores por fecha
    long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    // Tokens inactivos antiguos
    @Query("SELECT rt FROM RefreshTokenEntity rt " +
            "WHERE rt.activo = false AND rt.ultimoUso < :fecha")
    List<RefreshTokenEntity> findInactiveOlderThan(@Param("fecha") LocalDateTime fecha);

    // Estadísticas de tokens
    //noinspection JpaQlInspection
    @SuppressWarnings("JpaQlInspection")
    @Query("SELECT " +
            "COUNT(rt) as total, " +
            "SUM(CASE WHEN rt.activo = true THEN 1 ELSE 0 END) as activos, " +
            "SUM(CASE WHEN rt.fechaExpiracion < CURRENT_TIMESTAMP THEN 1 ELSE 0 END) as expirados, " +
            "SUM(CASE WHEN rt.activo = false THEN 1 ELSE 0 END) as revocados, " +
            "SUM(CASE WHEN rt.fechaExpiracion BETWEEN CURRENT_TIMESTAMP AND :fechaExpiracion24h THEN 1 ELSE 0 END) as porExpirar24h, " +
            "COUNT(DISTINCT rt.username) as sesionesUnicas, " +
            "COUNT(DISTINCT rt.deviceInfo) as dispositivosUnicos, " +
            "CAST(COUNT(rt) AS DOUBLE) / NULLIF(COUNT(DISTINCT rt.username), 0) as promedio " +
            "FROM RefreshTokenEntity rt")
    Object[] getTokenEstadisticas(@Param("fechaExpiracion24h") LocalDateTime fechaExpiracion24h);

    // Estadísticas por dispositivo
    @Query("SELECT rt.deviceInfo, COUNT(rt) FROM RefreshTokenEntity rt " +
            "GROUP BY rt.deviceInfo ORDER BY COUNT(rt) DESC")
    List<Object[]> getDeviceStatistics();

    // Dispositivos sospechosos - simulado
    @Query("SELECT " +
            "rt.deviceInfo as deviceId, " +
            "rt.deviceInfo as deviceInfo, " +
            "rt.ipAddress as ultimaIp, " +
            "COUNT(DISTINCT rt.username) as usuariosDiferentes, " +
            "0 as loginsFallidos, " +
            "'Múltiples usuarios en mismo dispositivo' as razonSospecha, " +
            "MIN(rt.fechaCreacion) as primeraActividad, " +
            "MAX(rt.ultimoUso) as ultimaActividad " +
            "FROM RefreshTokenEntity rt " +
            "GROUP BY rt.deviceInfo, rt.ipAddress " +
            "HAVING COUNT(DISTINCT rt.username) > 1")
    List<Object[]> findSuspiciousDevices();
}