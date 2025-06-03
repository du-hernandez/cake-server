package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.OcasionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OcasionRepository extends JpaRepository<OcasionEntity, Integer> {

    // Buscar por nombre (case insensitive)
    Optional<OcasionEntity> findByNombreIgnoreCase(String nombre);

    // Verificar si existe por nombre
    boolean existsByNombreIgnoreCase(String nombre);

    // Buscar todas las ocasiones activas
    List<OcasionEntity> findByEstado(Byte estado);

    // Buscar ocasiones activas con paginación
    Page<OcasionEntity> findByEstado(Byte estado, Pageable pageable);

    // Búsqueda por nombre con LIKE
    @Query("SELECT o FROM OcasionEntity o " +
            "WHERE LOWER(o.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<OcasionEntity> buscarPorNombre(@Param("nombre") String nombre);

    // Ocasiones asociadas a una torta
    @Query("SELECT DISTINCT o FROM OcasionEntity o " +
            "JOIN o.tortaOcasiones to " +
            "WHERE to.torta.id = :tortaId " +
            "AND to.estado = 1")
    List<OcasionEntity> findByTortaId(@Param("tortaId") Integer tortaId);

    // Ocasiones más populares (más tortas asociadas)
    @Query("SELECT o.id, o.nombre, COUNT(to.torta) as cantidad " +
            "FROM OcasionEntity o " +
            "LEFT JOIN o.tortaOcasiones to " +
            "WHERE o.estado = 1 AND to.estado = 1 " +
            "GROUP BY o.id, o.nombre " +
            "ORDER BY cantidad DESC")
    List<Object[]> findOcasionesMasPopulares(Pageable pageable);

    // Ocasiones sin tortas asociadas
    @Query("SELECT o FROM OcasionEntity o " +
            "WHERE o.id NOT IN (" +
            "  SELECT DISTINCT to.ocasion.id FROM TortaOcasionEntity to" +
            ")")
    List<OcasionEntity> findOcasionesSinTortas();
}
