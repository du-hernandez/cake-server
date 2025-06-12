package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.ImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<ImagenEntity, Integer> {

    // Buscar todas las imágenes de una torta
    List<ImagenEntity> findByTortaId(Integer tortaId);

    // Buscar imágenes por torta ordenadas por fecha
    @Query("SELECT i FROM ImagenEntity i " +
            "WHERE i.torta.id = :tortaId " +
            "ORDER BY i.fechaCreado DESC")
    List<ImagenEntity> findByTortaIdOrderByFechaCreadoDesc(@Param("tortaId") Integer tortaId);

    // Contar imágenes por torta
    @Query("SELECT COUNT(i) FROM ImagenEntity i WHERE i.torta.id = :tortaId")
    Long countByTortaId(@Param("tortaId") Integer tortaId);

    // Verificar si existe una imagen con una URL específica
    boolean existsByUrl(String url);

    // Buscar imágenes huérfanas (sin torta asociada)
    @Query("SELECT i FROM ImagenEntity i WHERE i.torta IS NULL")
    List<ImagenEntity> findOrphanImages();

    // Eliminar imágenes huérfanas más antiguas que cierta fecha
    @Query("DELETE FROM ImagenEntity i " +
            "WHERE i.torta IS NULL " +
            "AND i.fechaCreado < :fecha")
    void deleteOrphanImagesOlderThan(@Param("fecha") LocalDateTime fecha);
}
