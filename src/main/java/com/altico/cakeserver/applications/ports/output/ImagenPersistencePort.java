package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.Imagen;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Imágenes
 */
public interface ImagenPersistencePort {

    // Operaciones CRUD
    Imagen save(Imagen imagen);
    Optional<Imagen> findById(Integer id);
    void deleteById(Integer id);

    // Búsquedas
    List<Imagen> findByTortaId(Integer tortaId);
    List<Imagen> findAll();

    // Operaciones especiales
    void deleteByTortaId(Integer tortaId);
    List<Imagen> findOrphanImages();
    int deleteOrphanImagesOlderThan(LocalDateTime fecha);

    // Validaciones
    boolean existsByUrl(String url);
    boolean existsByIdAndTortaId(Integer id, Integer tortaId);
    long countByTortaId(Integer tortaId);
}
