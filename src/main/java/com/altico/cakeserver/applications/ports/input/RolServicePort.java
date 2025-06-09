package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.domain.model.SincronizacionResult;
import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.applications.ports.input.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada para el servicio de gestión de roles
 */
public interface RolServicePort {

    // Operaciones CRUD básicas
    RolCompleto crearRol(CreateRolCommand command);
    RolCompleto obtenerPorId(Integer id);
    RolCompleto obtenerPorIdConPermisos(Integer id);
    RolCompleto obtenerPorNombre(String nombre);
    RolCompleto actualizarRol(Integer id, UpdateRolCommand command);
    void eliminarRol(Integer id);

    // Búsquedas y listados
    Page<RolCompleto> listarRoles(Pageable pageable, Boolean activo);
    List<RolCompleto> buscarRoles(String termino);
    List<RolCompleto> listarRolesActivos();
    List<RolCompleto> obtenerJerarquiaRoles();

    // Gestión de estado
    RolCompleto activarRol(Integer id);
    RolCompleto desactivarRol(Integer id);

    // Gestión de permisos
    RolCompleto asignarPermiso(Integer rolId, Integer permisoId);
    RolCompleto removerPermiso(Integer rolId, Integer permisoId);
    RolCompleto sincronizarPermisos(Integer rolId, List<Integer> permisoIds);

    // Consultas con usuarios
    List<UsuarioCompleto> obtenerUsuariosConRol(Integer rolId);
    List<RolCompleto> obtenerRolesPorUsuario(Long usuarioId);
    boolean usuarioTieneRol(Long usuarioId, Integer rolId);

    // Validaciones de permisos
    boolean rolTienePermiso(Integer rolId, String recurso, String accion);
    List<String> obtenerPermisosDelRol(Integer rolId);
    RolCompleto obtenerRolPrincipalDelUsuario(Long usuarioId);

    // Operaciones de sincronización y mantenimiento
    SincronizacionResult sincronizarPermisosSistema();
    void crearRolesSistemaBasicos();
    void actualizarJerarquiaRoles();

    // Estadísticas y reportes
    Map<String, Long> obtenerEstadisticasRoles();
    List<RolCompleto> obtenerRolesSinUsuarios();
    List<RolCompleto> obtenerRolesSinPermisos();

    // Validaciones
    boolean existeRol(String nombre);
    boolean puedeEliminarRol(Integer rolId);
}
