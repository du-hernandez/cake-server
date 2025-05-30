package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.Ocasion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Ocasiones
 */
public interface OcasionPersistencePort {

    // Operaciones CRUD
    Ocasion save(Ocasion ocasion);
    Optional<Ocasion> findById(Integer id);
    void deleteById(Integer id);
    boolean existsById(Integer id);

    // Búsquedas
    List<Ocasion> findAll();
    Page<Ocasion> findAll(Pageable pageable);
    List<Ocasion> findByEstado(boolean activo);
    List<Ocasion> findByNombreContaining(String nombre);
    Optional<Ocasion> findByNombre(String nombre);

    // Validaciones
    boolean existsByNombre(String nombre);

    // Consultas especiales
    List<Map<String, Object>> findMostPopular(int limit);
    List<Ocasion> findWithoutTortas();
    long countByEstado(boolean activo);
}
