package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;


import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.TortaEntity;
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
public interface TortaRepository extends JpaRepository<TortaEntity, Integer> {

    // Búsqueda con fetch join para evitar N+1 queries
    @Query("SELECT DISTINCT t FROM TortaEntity t " +
            "LEFT JOIN FETCH t.tortaOcasiones to " +
            "LEFT JOIN FETCH to.ocasion " +
            "WHERE t.id = :id")
    Optional<TortaEntity> findByIdWithOcasiones(@Param("id") Integer id);

    // Búsqueda por descripción con full text search de PostgreSQL
    @Query(value = "SELECT * FROM torta t " +
            "WHERE to_tsvector('spanish', t.descripcion) @@ plainto_tsquery('spanish', :searchTerm)",
            nativeQuery = true)
    List<TortaEntity> searchByDescripcion(@Param("searchTerm") String searchTerm);

    // Búsqueda por ocasión con paginación
    @Query("SELECT DISTINCT t FROM TortaEntity t " +
            "JOIN t.tortaOcasiones to " +
            "WHERE to.ocasion.id = :ocasionId " +
            "AND to.estado = 1")
    Page<TortaEntity> findByOcasionId(@Param("ocasionId") Integer ocasionId, Pageable pageable);

    // Búsqueda por múltiples ocasiones
    @Query("SELECT DISTINCT t FROM TortaEntity t " +
            "JOIN t.tortaOcasiones to " +
            "WHERE to.ocasion.id IN :ocasionIds " +
            "AND to.estado = 1")
    List<TortaEntity> findByOcasionIds(@Param("ocasionIds") List<Integer> ocasionIds);

    // Tortas creadas en un rango de fechas
    @Query("SELECT t FROM TortaEntity t " +
            "WHERE t.fechaCreado BETWEEN :startDate AND :endDate " +
            "ORDER BY t.fechaCreado DESC")
    List<TortaEntity> findByFechaCreadoBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Estadísticas usando funciones de agregación de PostgreSQL
    @Query(value = "SELECT " +
            "COUNT(DISTINCT t.id) as total_tortas, " +
            "COUNT(DISTINCT to.ocasion_id) as total_ocasiones, " +
            "COUNT(DISTINCT i.id) as total_imagenes " +
            "FROM torta t " +
            "LEFT JOIN torta_ocasion to ON t.id = to.torta_id " +
            "LEFT JOIN imagenes i ON t.id = i.fk_torta",
            nativeQuery = true)
    Object[] getEstadisticas();

    // Búsqueda con filtros complejos
    @Query("SELECT DISTINCT t FROM TortaEntity t " +
            "LEFT JOIN t.tortaOcasiones to " +
            "WHERE (:descripcion IS NULL OR LOWER(t.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) " +
            "AND (:ocasionId IS NULL OR to.ocasion.id = :ocasionId) " +
            "AND (:tieneImagen IS NULL OR " +
            "     (:tieneImagen = true AND t.imagen IS NOT NULL) OR " +
            "     (:tieneImagen = false AND t.imagen IS NULL))")
    Page<TortaEntity> findWithFilters(
            @Param("descripcion") String descripcion,
            @Param("ocasionId") Integer ocasionId,
            @Param("tieneImagen") Boolean tieneImagen,
            Pageable pageable
    );
}