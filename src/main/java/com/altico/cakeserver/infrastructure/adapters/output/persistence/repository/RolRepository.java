package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RolEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public interface RolRepository extends JpaRepository<RolEntity, Integer> {

    // Búsquedas básicas
    boolean existsByNombre(String nombre);
    Optional<RolEntity> findByNombre(String nombre);
    List<RolEntity> findByActivo(boolean activo);

    // Buscar por prioridad
    List<RolEntity> findByPrioridadLessThanEqualOrderByPrioridadAsc(Integer prioridad);

    // Búsqueda con fetch join para permisos
    @Query("SELECT DISTINCT r FROM RolEntity r " +
            "LEFT JOIN FETCH r.permisos " +
            "WHERE r.id = :id")
    Optional<RolEntity> findByIdWithPermisos(@Param("id") Integer id);

    // Búsqueda con filtros
    @Query("SELECT r FROM RolEntity r " +
            "WHERE (:activo IS NULL OR r.activo = :activo)")
    Page<RolEntity> findAllWithFilters(@Param("activo") Boolean activo, Pageable pageable);

    // Búsqueda por nombre o descripción
    @Query("SELECT r FROM RolEntity r " +
            "WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "OR LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<RolEntity> searchByNombreOrDescripcion(@Param("termino") String termino);

    // Obtener roles ordenados por prioridad
    @Query("SELECT r FROM RolEntity r ORDER BY r.prioridad ASC, r.nombre ASC")
    List<RolEntity> findAllOrderByPrioridad();

    // Roles sin permisos
    @Query("SELECT r FROM RolEntity r " +
            "WHERE r.permisos IS EMPTY OR SIZE(r.permisos) = 0")
    List<RolEntity> findWithoutPermisos();

    // ✅ CORREGIDO: Roles sin usuarios - consulta JPQL
    @Query("SELECT r FROM RolEntity r " +
            "WHERE r.id NOT IN (" +
            "  SELECT DISTINCT ur.id FROM UsuarioEntity u " +
            "  JOIN u.roles ur" +
            ")")
    List<RolEntity> findWithoutUsers();

    // ✅ CORREGIDO: Usuarios por rol - consulta JPQL mejorada
    @Query("SELECT u FROM UsuarioEntity u " +
            "JOIN u.roles r " +
            "WHERE r.nombre = :rolNombre")
    List<UsuarioEntity> findUsersByRolNombre(@Param("rolNombre") String rolNombre);

    // ✅ CORREGIDO: Roles por usuario - consulta corregida
    @Query("SELECT r FROM RolEntity r " +
            "JOIN UsuarioEntity u ON r MEMBER OF u.roles " +
            "WHERE u.id = :usuarioId")
    List<RolEntity> findRolesByUserId(@Param("usuarioId") Long usuarioId);

    // Verificar si rol tiene permiso específico
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
            "FROM RolEntity r JOIN r.permisos rp " +
            "WHERE r.id = :rolId AND rp.id = :permisoId")
    boolean rolHasPermiso(@Param("rolId") Integer rolId, @Param("permisoId") Integer permisoId);

    // ✅ CORREGIDO: Estadísticas de roles - consulta JPQL
    @Query("SELECT " +
            "'total' as tipo, COUNT(r) as cantidad FROM RolEntity r " +
            "UNION ALL " +
            "SELECT 'activos' as tipo, COUNT(r) as cantidad FROM RolEntity r WHERE r.activo = true " +
            "UNION ALL " +
            "SELECT 'inactivos' as tipo, COUNT(r) as cantidad FROM RolEntity r WHERE r.activo = false " +
            "UNION ALL " +
            "SELECT 'conPermisos' as tipo, COUNT(DISTINCT r) as cantidad FROM RolEntity r " +
            "WHERE SIZE(r.permisos) > 0 " +
            "UNION ALL " +
            "SELECT 'sinPermisos' as tipo, COUNT(r) as cantidad FROM RolEntity r " +
            "WHERE SIZE(r.permisos) = 0")
    List<Object[]> getRolEstadisticas();

    // Contadores específicos
    long countByActivo(boolean activo);
    long countByPrioridad(int prioridad);

    // Roles por rango de prioridad
    @Query("SELECT r FROM RolEntity r " +
            "WHERE r.prioridad BETWEEN :minPrioridad AND :maxPrioridad " +
            "ORDER BY r.prioridad ASC")
    List<RolEntity> findByPrioridadBetween(@Param("minPrioridad") int minPrioridad,
                                           @Param("maxPrioridad") int maxPrioridad);

    // Verificar prioridad única
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM RolEntity r WHERE r.prioridad = :prioridad AND r.id != :excludeId")
    boolean existsByPrioridadAndIdNot(@Param("prioridad") int prioridad, @Param("excludeId") Integer excludeId);

    // ✅ CORREGIDO: Roles más utilizados - consulta nativa simplificada
    @Query(value = "SELECT r.nombre, COUNT(ur.usuario_id) as cantidad " +
            "FROM roles r " +
            "LEFT JOIN usuario_roles ur ON r.id = ur.rol_id " +
            "GROUP BY r.id, r.nombre " +
            "ORDER BY cantidad DESC", nativeQuery = true)
    List<Object[]> findMostUsedRoles();

    // Buscar por nombre exacto (case insensitive)
    @Query("SELECT r FROM RolEntity r WHERE LOWER(r.nombre) = LOWER(:nombre)")
    Optional<RolEntity> findByNombreIgnoreCase(@Param("nombre") String nombre);
}