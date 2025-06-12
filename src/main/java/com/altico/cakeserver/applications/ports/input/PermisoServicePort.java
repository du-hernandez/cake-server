package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.Permiso;
import com.altico.cakeserver.applications.ports.input.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada para el servicio de gestión de permisos
 */
public interface PermisoServicePort {

    // Operaciones CRUD básicas
    Permiso crearPermiso(CreatePermisoCommand command);
    Permiso obtenerPorId(Integer id);
    Permiso actualizarPermiso(Integer id, UpdatePermisoCommand command);
    void eliminarPermiso(Integer id);

    // Búsquedas y listados
    Page<Permiso> listarPermisos(Pageable pageable, String recurso, String accion, Boolean activo);
    List<Permiso> buscarPermisos(String termino);
    List<Permiso> obtenerPermisosPorRecurso(String recurso);
    List<Permiso> obtenerPermisosPorAccion(String accion);

    // Gestión de estado
    Permiso activarPermiso(Integer id);
    Permiso desactivarPermiso(Integer id);

    // Operaciones de consulta
    List<String> listarRecursos();
    List<String> listarAcciones();
    List<String> listarAccionesPorRecurso(String recurso);
    Map<String, List<String>> obtenerMatrizRecursosAcciones();

    // Validaciones
    boolean existePermiso(String recurso, String accion);
    boolean existePorNombre(String nombre);
    Permiso obtenerPorRecursoYAccion(String recurso, String accion);

    // Operaciones de sincronización
    List<Permiso> sincronizarPermisosSistema();
    void crearPermisosSistemaBasicos();

    // Estadísticas
    Map<String, Long> obtenerEstadisticasPermisos();
    List<Permiso> obtenerPermisosNoUtilizados();
}