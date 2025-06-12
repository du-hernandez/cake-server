package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.domain.model.UsuarioCompleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Roles completos
 */
public interface RolPersistencePort {

    // Operaciones CRUD básicas
    RolCompleto save(RolCompleto rol);
    Optional<RolCompleto> findById(Integer id);
    Optional<RolCompleto> findByIdWithPermisos(Integer id);
    Optional<RolCompleto> findByNombre(String nombre);
    void deleteById(Integer id);
    boolean existsById(Integer id);

    // Validaciones
    boolean existsByNombre(String nombre);

    // Búsquedas y listados
    Page<RolCompleto> findAllWithFilters(Pageable pageable, Boolean activo);
    List<RolCompleto> findAll();
    List<RolCompleto> findByActivo(boolean activo);
    List<RolCompleto> searchByNombreOrDescripcion(String termino);

    // Consultas jerárquicas
    List<RolCompleto> findAllOrderByPrioridad();
    List<RolCompleto> findWithoutPermisos();
    List<RolCompleto> findWithoutUsers();

    // Relaciones con usuarios
    List<UsuarioCompleto> findUsersByRolId(Integer rolId);
    List<RolCompleto> findRolesByUserId(Long usuarioId);

    // Gestión de permisos
    void addPermisoToRol(Integer rolId, Integer permisoId);
    void removePermisoFromRol(Integer rolId, Integer permisoId);
    boolean rolHasPermiso(Integer rolId, Integer permisoId);

    // Estadísticas
    Map<String, Long> getRolEstadisticas();
}
