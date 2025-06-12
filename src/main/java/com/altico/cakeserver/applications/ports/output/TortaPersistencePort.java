package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.Torta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Tortas
 */
public interface TortaPersistencePort {

    // Operaciones CRUD
    Torta save(Torta torta);
    Optional<Torta> findById(Integer id);
    Optional<Torta> findByIdWithRelations(Integer id);
    void deleteById(Integer id);
    boolean existsById(Integer id);

    // Búsquedas
    List<Torta> findAll();
    Page<Torta> findAll(Pageable pageable);
    List<Torta> findByDescripcionContaining(String descripcion);
    List<Torta> findByOcasionId(Integer ocasionId);
    List<Torta> findByOcasionIds(List<Integer> ocasionIds);

    // Consultas especiales
    List<Torta> findRecent(int limit);
    long count();

    // Operaciones con relaciones
    void addOcasion(Integer tortaId, Integer ocasionId);
    void removeOcasion(Integer tortaId, Integer ocasionId);
    void updateOcasionStatus(Integer tortaId, Integer ocasionId, boolean activo);
}
