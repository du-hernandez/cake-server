package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // ============== NUEVAS CONSULTAS PARA ADMINISTRACIÓN ==============

    // Búsqueda con filtros avanzados
    @Query("SELECT u FROM UsuarioEntity u " +
            "WHERE (:activo IS NULL OR u.activo = :activo) " +
            "AND (:rol IS NULL OR :rol MEMBER OF u.roles)")
    Page<UsuarioEntity> findAllWithFilters(@Param("activo") Boolean activo,
                                           @Param("rol") String rol,
                                           Pageable pageable);

    // Búsqueda por término (username o email)
    @Query("SELECT u FROM UsuarioEntity u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<UsuarioEntity> searchByUsernameOrEmail(@Param("termino") String termino);

    // Usuarios por rol específico
    @Query("SELECT u FROM UsuarioEntity u WHERE :rol MEMBER OF u.roles")
    List<UsuarioEntity> findByRoles(@Param("rol") String rol);

    // Usuarios sin roles
    @Query("SELECT u FROM UsuarioEntity u WHERE u.roles IS EMPTY OR SIZE(u.roles) = 0")
    List<UsuarioEntity> findWithoutRoles();

    // Usuarios inactivos después de cierta fecha
    @Query("SELECT u FROM UsuarioEntity u " +
            "WHERE u.activo = false AND u.fechaActualizado > :fecha")
    List<UsuarioEntity> findInactiveUsersAfterDate(@Param("fecha") LocalDateTime fecha);

    // Usuarios con múltiples sesiones (simulado - necesitaría tabla de sesiones)
    @Query("SELECT u FROM UsuarioEntity u WHERE SIZE(u.roles) > 1")
    List<UsuarioEntity> findUsersWithMultipleSessions();

    // Contadores por rol
    @Query("SELECT COUNT(u) FROM UsuarioEntity u WHERE :rol MEMBER OF u.roles")
    long countByRole(@Param("rol") String rol);

    @Query("SELECT COUNT(u) FROM UsuarioEntity u " +
            "WHERE :rol MEMBER OF u.roles AND u.activo = :activo")
    long countByRoleAndActive(@Param("rol") String rol, @Param("activo") boolean activo);

    // Estadísticas de usuarios
    @Query("SELECT " +
            "COUNT(u) as total, " +
            "SUM(CASE WHEN u.activo = true THEN 1 ELSE 0 END) as activos, " +
            "SUM(CASE WHEN u.activo = false THEN 1 ELSE 0 END) as inactivos, " +
            "SUM(CASE WHEN u.roles IS EMPTY THEN 1 ELSE 0 END) as sinRoles, " +
            "SUM(CASE WHEN SIZE(u.roles) > 1 THEN 1 ELSE 0 END) as conMultiplesRoles " +
            "FROM UsuarioEntity u")
    Object[] getUsuarioEstadisticas();

    // Usuarios creados en las últimas horas/días
    @Query("SELECT COUNT(u) FROM UsuarioEntity u " +
            "WHERE u.fechaCreado >= :fecha")
    long countCreatedAfter(@Param("fecha") LocalDateTime fecha);

    // Usuarios por estado y fecha de creación
    List<UsuarioEntity> findByActivoAndFechaCreadoAfter(boolean activo, LocalDateTime fecha);

    // Buscar usuarios por email exacto
    @Query("SELECT u FROM UsuarioEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UsuarioEntity> findByEmailIgnoreCase(@Param("email") String email);

    // Usuarios ordenados por fecha de creación
    @Query("SELECT u FROM UsuarioEntity u ORDER BY u.fechaCreado DESC")
    List<UsuarioEntity> findAllOrderByFechaCreadoDesc();

    // Usuarios con rol específico ordenados por username
    @Query("SELECT u FROM UsuarioEntity u " +
            "WHERE :rol MEMBER OF u.roles " +
            "ORDER BY u.username ASC")
    List<UsuarioEntity> findByRoleOrderByUsername(@Param("rol") String rol);

    // Verificar si usuario tiene rol específico
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UsuarioEntity u WHERE u.id = :usuarioId AND :rol MEMBER OF u.roles")
    boolean userHasRole(@Param("usuarioId") Long usuarioId, @Param("rol") String rol);

    // Buscar usuarios creados entre fechas
    @Query("SELECT u FROM UsuarioEntity u " +
            "WHERE u.fechaCreado BETWEEN :fechaInicio AND :fechaFin")
    List<UsuarioEntity> findByFechaCreadoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                 @Param("fechaFin") LocalDateTime fechaFin);

    // Usuarios más recientes
    @Query("SELECT u FROM UsuarioEntity u ORDER BY u.fechaCreado DESC")
    Page<UsuarioEntity> findMostRecent(Pageable pageable);

    // Buscar por ID con verificación de existencia
    @Query("SELECT u FROM UsuarioEntity u WHERE u.id = :id AND u.activo = true")
    Optional<UsuarioEntity> findByIdAndActive(@Param("id") Long id);
}