package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.Permiso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Permisos
 */
public interface PermisoPersistencePort {

    // Operaciones CRUD
    Permiso save(Permiso permiso);
    Optional<Permiso> findById(Integer id);
    void deleteById(Integer id);
    boolean existsById(Integer id);

    // Búsquedas y listados
    Page<Permiso> findAllWithFilters(Pageable pageable, String recurso, String accion, Boolean activo);
    List<Permiso> findAll();
    List<Permiso> searchByNombreOrDescripcion(String termino);
    List<Permiso> findByRecurso(String recurso);
    List<Permiso> findByAccion(String accion);

    // Validaciones de duplicados
    boolean existsByNombre(String nombre);
    boolean existsByRecursoAndAccion(String recurso, String accion);
    Optional<Permiso> findByRecursoAndAccion(String recurso, String accion);

    // Consultas especiales
    List<String> findAllRecursos();
    List<String> findAllAcciones();
    List<String> findAccionesByRecurso(String recurso);
    Map<String, List<String>> getRecursoAccionMatrix();

    // Validaciones de uso
    boolean isPermisoInUse(Integer permisoId);
    List<Permiso> findUnusedPermisos();

    // Estadísticas
    Map<String, Long> getPermisoEstadisticas();
}