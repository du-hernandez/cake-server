package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.PermisoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<PermisoEntity, Integer> {

    // Búsquedas básicas
    boolean existsByNombre(String nombre);
    boolean existsByRecursoAndAccion(String recurso, String accion);
    Optional<PermisoEntity> findByRecursoAndAccion(String recurso, String accion);

    // Búsquedas por recurso y acción
    List<PermisoEntity> findByRecurso(String recurso);
    List<PermisoEntity> findByAccion(String accion);
    List<PermisoEntity> findByActivo(boolean activo);

    // Búsqueda con filtros
    @Query("SELECT p FROM PermisoEntity p " +
            "WHERE (:recurso IS NULL OR p.recurso = :recurso) " +
            "AND (:accion IS NULL OR p.accion = :accion) " +
            "AND (:activo IS NULL OR p.activo = :activo)")
    Page<PermisoEntity> findAllWithFilters(
            @Param("recurso") String recurso,
            @Param("accion") String accion,
            @Param("activo") Boolean activo,
            Pageable pageable
    );

    // Búsqueda por nombre o descripción
    @Query("SELECT p FROM PermisoEntity p " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
            "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<PermisoEntity> searchByNombreOrDescripcion(@Param("termino") String termino);

    // Obtener todos los recursos únicos
    @Query("SELECT DISTINCT p.recurso FROM PermisoEntity p ORDER BY p.recurso")
    List<String> findAllRecursos();

    // Obtener todas las acciones únicas
    @Query("SELECT DISTINCT p.accion FROM PermisoEntity p ORDER BY p.accion")
    List<String> findAllAcciones();

    // Obtener acciones por recurso
    @Query("SELECT DISTINCT p.accion FROM PermisoEntity p WHERE p.recurso = :recurso ORDER BY p.accion")
    List<String> findAccionesByRecurso(@Param("recurso") String recurso);

    // Verificar si un permiso está en uso (tiene roles asociados)
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
            "FROM RolEntity r JOIN r.permisos rp WHERE rp.id = :permisoId")
    boolean isPermisoInUse(@Param("permisoId") Integer permisoId);

    // Encontrar permisos no utilizados
    @Query("SELECT p FROM PermisoEntity p " +
            "WHERE p.id NOT IN (SELECT rp.id FROM RolEntity r JOIN r.permisos rp)")
    List<PermisoEntity> findUnusedPermisos();

    // Estadísticas de permisos
    @Query("SELECT " +
            "'total' as tipo, COUNT(p) as cantidad FROM PermisoEntity p " +
            "UNION ALL " +
            "SELECT 'activos' as tipo, COUNT(p) as cantidad FROM PermisoEntity p WHERE p.activo = true " +
            "UNION ALL " +
            "SELECT 'inactivos' as tipo, COUNT(p) as cantidad FROM PermisoEntity p WHERE p.activo = false " +
            "UNION ALL " +
            "SELECT 'enUso' as tipo, COUNT(DISTINCT rp.id) as cantidad FROM RolEntity r JOIN r.permisos rp " +
            "UNION ALL " +
            "SELECT 'sinUso' as tipo, COUNT(p) as cantidad FROM PermisoEntity p " +
            "WHERE p.id NOT IN (SELECT rp.id FROM RolEntity r JOIN r.permisos rp)")
    List<Object[]> getPermisoEstadisticas();

    // Contadores específicos
    long countByActivo(boolean activo);
    long countByRecurso(String recurso);
    long countByAccion(String accion);

    // Permisos por recurso ordenados
    @Query("SELECT p FROM PermisoEntity p WHERE p.recurso = :recurso ORDER BY p.accion")
    List<PermisoEntity> findByRecursoOrderByAccion(@Param("recurso") String recurso);

    // Matriz de recursos y acciones
    @Query("SELECT p.recurso, p.accion FROM PermisoEntity p WHERE p.activo = true ORDER BY p.recurso, p.accion")
    List<Object[]> getRecursoAccionMatrix();
}
