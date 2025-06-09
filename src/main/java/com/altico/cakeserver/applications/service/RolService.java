package com.altico.cakeserver.applications.service;

import com.altico.cakeserver.applications.ports.input.RolServicePort;
import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.applications.ports.output.*;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RolService implements RolServicePort {

    private final RolPersistencePort rolPersistence;
    private final PermisoPersistencePort permisoPersistence;
    private final UsuarioPersistencePort usuarioPersistence;

    @Override
    public RolCompleto crearRol(CreateRolCommand command) {
        log.info("Creando rol: {}", command.nombre());

        // Validar que no exista un rol con el mismo nombre
        if (rolPersistence.existsByNombre(command.nombre())) {
            throw new DuplicateRoleException(command.nombre());
        }

        // Validar prioridad única (opcional, dependiendo de reglas de negocio)
        validarPrioridadUnica(command.prioridad());

        // Crear rol base
        RolCompleto rol = RolCompleto.crear(command.nombre(), command.descripcion(), command.prioridad());
        RolCompleto rolGuardado = rolPersistence.save(rol);

        // Asignar permisos si se especificaron
        if (command.permisoIds() != null && !command.permisoIds().isEmpty()) {
            for (Integer permisoId : command.permisoIds()) {
                rolGuardado = asignarPermisoInterno(rolGuardado, permisoId);
            }
        }

        log.info("Rol creado exitosamente: {}", command.nombre());
        return rolGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public RolCompleto obtenerPorId(Integer id) {
        return rolPersistence.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RolCompleto obtenerPorIdConPermisos(Integer id) {
        return rolPersistence.findByIdWithPermisos(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RolCompleto obtenerPorNombre(String nombre) {
        return rolPersistence.findByNombre(nombre)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado: " + nombre));
    }

    @Override
    public RolCompleto actualizarRol(Integer id, UpdateRolCommand command) {
        log.info("Actualizando rol con ID: {}", id);

        RolCompleto rol = obtenerPorId(id);

        // Validar nombre único si cambió
        if (command.nombre() != null && !command.nombre().equals(rol.getNombre())) {
            if (rolPersistence.existsByNombre(command.nombre())) {
                throw new DuplicateRoleException(command.nombre());
            }
        }

        // Validar prioridad si cambió
        if (command.prioridad() != null && !command.prioridad().equals(rol.getPrioridad())) {
            validarPrioridadUnica(command.prioridad());
        }

        // Crear rol actualizado
        RolCompleto rolActualizado = new RolCompleto(
                rol.getId(),
                command.nombre() != null ? command.nombre() : rol.getNombre(),
                command.descripcion() != null ? command.descripcion() : rol.getDescripcion(),
                command.prioridad() != null ? command.prioridad() : rol.getPrioridad(),
                rol.isActivo(),
                rol.getPermisos(),
                rol.getFechaCreado(),
                LocalDateTime.now()
        );

        return rolPersistence.save(rolActualizado);
    }

    @Override
    public void eliminarRol(Integer id) {
        log.info("Eliminando rol con ID: {}", id);

        RolCompleto rol = obtenerPorId(id);

        // Validar si se puede eliminar
        if (!puedeEliminarRol(id)) {
            throw new RoleInUseException(rol.getNombre());
        }

        // Validar roles críticos del sistema
        if (esRolSistema(rol.getNombre())) {
            throw new BusinessRuleViolationException("No se puede eliminar un rol del sistema");
        }

        rolPersistence.deleteById(id);
        log.info("Rol eliminado: {}", rol.getNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolCompleto> listarRoles(Pageable pageable, Boolean activo) {
        return rolPersistence.findAllWithFilters(pageable, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> buscarRoles(String termino) {
        return rolPersistence.searchByNombreOrDescripcion(termino);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> listarRolesActivos() {
        return rolPersistence.findByActivo(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> obtenerJerarquiaRoles() {
        return rolPersistence.findAllOrderByPrioridad();
    }

    @Override
    public RolCompleto activarRol(Integer id) {
        log.info("Activando rol con ID: {}", id);

        RolCompleto rol = obtenerPorId(id);
        if (rol.isActivo()) {
            return rol;
        }

        RolCompleto rolActivo = rol.activar();
        return rolPersistence.save(rolActivo);
    }

    @Override
    public RolCompleto desactivarRol(Integer id) {
        log.info("Desactivando rol con ID: {}", id);

        RolCompleto rol = obtenerPorId(id);

        // Validar que no sea un rol crítico
        if (esRolSistema(rol.getNombre())) {
            throw new BusinessRuleViolationException("No se puede desactivar un rol del sistema");
        }

        if (!rol.isActivo()) {
            return rol;
        }

        RolCompleto rolInactivo = rol.desactivar();
        return rolPersistence.save(rolInactivo);
    }

    @Override
    public RolCompleto asignarPermiso(Integer rolId, Integer permisoId) {
        log.info("Asignando permiso {} al rol {}", permisoId, rolId);

        RolCompleto rol = obtenerPorIdConPermisos(rolId);
        return asignarPermisoInterno(rol, permisoId);
    }

    @Override
    public RolCompleto removerPermiso(Integer rolId, Integer permisoId) {
        log.info("Removiendo permiso {} del rol {}", permisoId, rolId);

        RolCompleto rol = obtenerPorIdConPermisos(rolId);

        // Buscar el permiso
        Permiso permiso = permisoPersistence.findById(permisoId)
                .orElseThrow(() -> new PermissionNotFoundException(permisoId));

        // Verificar que el rol tenga el permiso
        if (!rol.getPermisos().contains(permiso)) {
            throw new BusinessRuleViolationException("El rol no tiene asignado este permiso");
        }

        RolCompleto rolActualizado = rol.removerPermiso(permiso);
        RolCompleto resultado = rolPersistence.save(rolActualizado);

        // Actualizar en la base de datos
        rolPersistence.removePermisoFromRol(rolId, permisoId);

        return resultado;
    }

    @Override
    public RolCompleto sincronizarPermisos(Integer rolId, List<Integer> permisoIds) {
        log.info("Sincronizando permisos para rol: {}", rolId);

        RolCompleto rol = obtenerPorIdConPermisos(rolId);

        // Obtener permisos actuales
        Set<Integer> permisosActuales = rol.getPermisos().stream()
                .map(Permiso::getId)
                .collect(Collectors.toSet());

        Set<Integer> permisosNuevos = new HashSet<>(permisoIds);

        // Remover permisos que ya no están en la lista
        for (Integer permisoId : permisosActuales) {
            if (!permisosNuevos.contains(permisoId)) {
                rolPersistence.removePermisoFromRol(rolId, permisoId);
            }
        }

        // Agregar permisos nuevos
        for (Integer permisoId : permisosNuevos) {
            if (!permisosActuales.contains(permisoId)) {
                rolPersistence.addPermisoToRol(rolId, permisoId);
            }
        }

        return obtenerPorIdConPermisos(rolId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> obtenerUsuariosConRol(Integer rolId) {
        return rolPersistence.findUsersByRolId(rolId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> obtenerRolesPorUsuario(Long usuarioId) {
        return rolPersistence.findRolesByUserId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usuarioTieneRol(Long usuarioId, Integer rolId) {
        return obtenerRolesPorUsuario(usuarioId).stream()
                .anyMatch(rol -> rol.getId().equals(rolId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean rolTienePermiso(Integer rolId, String recurso, String accion) {
        RolCompleto rol = obtenerPorIdConPermisos(rolId);
        return rol.tienePermiso(recurso, accion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerPermisosDelRol(Integer rolId) {
        RolCompleto rol = obtenerPorIdConPermisos(rolId);
        return new ArrayList<>(rol.getPermisosCodigosCompletos());
    }

    @Override
    @Transactional(readOnly = true)
    public RolCompleto obtenerRolPrincipalDelUsuario(Long usuarioId) {
        return obtenerRolesPorUsuario(usuarioId).stream()
                .filter(RolCompleto::isActivo)
                .min(Comparator.comparing(RolCompleto::getPrioridad))
                .orElse(null);
    }

    @Override
    public SincronizacionResult sincronizarPermisosSistema() {
        log.info("Sincronizando permisos del sistema en roles");

        // Implementar lógica de sincronización automática
        // Por ahora retornamos un resultado vacío
        return new SincronizacionResult(
                "Sincronización completada",
                0, 0, 0,
                List.of(), List.of(),
                LocalDateTime.now()
        );
    }

    @Override
    public void crearRolesSistemaBasicos() {
        log.info("Creando roles básicos del sistema");

        crearRolSiNoExiste("ROLE_SUPER_ADMIN", "Super Administrador con todos los permisos", 1);
        crearRolSiNoExiste("ROLE_ADMIN", "Administrador del sistema", 10);
        crearRolSiNoExiste("ROLE_MANAGER", "Gerente con permisos de gestión", 50);
        crearRolSiNoExiste("ROLE_USER", "Usuario regular con permisos básicos", 100);
        crearRolSiNoExiste("ROLE_VIEWER", "Solo lectura", 500);

        log.info("Roles básicos del sistema verificados");
    }

    @Override
    public void actualizarJerarquiaRoles() {
        log.info("Actualizando jerarquía de roles");
        // Implementar lógica de actualización de jerarquía si es necesario
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticasRoles() {
        return rolPersistence.getRolEstadisticas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> obtenerRolesSinUsuarios() {
        return rolPersistence.findWithoutUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> obtenerRolesSinPermisos() {
        return rolPersistence.findWithoutPermisos();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeRol(String nombre) {
        return rolPersistence.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeEliminarRol(Integer rolId) {
        // Un rol se puede eliminar si:
        // 1. No es un rol del sistema
        // 2. No tiene usuarios asignados
        // 3. No es el último rol ADMIN

        RolCompleto rol = obtenerPorId(rolId);

        if (esRolSistema(rol.getNombre())) {
            return false;
        }

        List<UsuarioCompleto> usuarios = obtenerUsuariosConRol(rolId);
        if (!usuarios.isEmpty()) {
            return false;
        }

        // Validar que no sea el último admin
        if (rol.getNombre().contains("ADMIN")) {
            long adminRoles = rolPersistence.findByActivo(true).stream()
                    .filter(r -> r.getNombre().contains("ADMIN"))
                    .count();
            return adminRoles > 1;
        }

        return true;
    }

    // ============== MÉTODOS AUXILIARES PRIVADOS ==============

    private RolCompleto asignarPermisoInterno(RolCompleto rol, Integer permisoId) {
        // Verificar que el permiso existe y está activo
        Permiso permiso = permisoPersistence.findById(permisoId)
                .orElseThrow(() -> new PermissionNotFoundException(permisoId));

        if (!permiso.isActivo()) {
            throw new BusinessRuleViolationException("No se puede asignar un permiso inactivo");
        }

        // Verificar que no esté ya asignado
        if (rol.getPermisos().contains(permiso)) {
            return rol; // Ya tiene el permiso
        }

        RolCompleto rolActualizado = rol.agregarPermiso(permiso);
        RolCompleto resultado = rolPersistence.save(rolActualizado);

        // Actualizar en la base de datos
        rolPersistence.addPermisoToRol(rol.getId(), permisoId);

        return resultado;
    }

    private void validarPrioridadUnica(int prioridad) {
        // Opcional: validar que la prioridad sea única
        // Esto depende de las reglas de negocio específicas
    }

    private boolean esRolSistema(String nombreRol) {
        return nombreRol.equals("ROLE_SUPER_ADMIN") ||
                nombreRol.equals("ROLE_ADMIN") ||
                nombreRol.equals("ROLE_USER") ||
                nombreRol.equals("ROLE_VIEWER");
    }

    private void crearRolSiNoExiste(String nombre, String descripcion, int prioridad) {
        if (!existeRol(nombre)) {
            try {
                CreateRolCommand command = new CreateRolCommand(nombre, descripcion, prioridad, Set.of());
                crearRol(command);
                log.info("Rol creado: {}", nombre);
            } catch (Exception e) {
                log.warn("Error al crear rol {}: {}", nombre, e.getMessage());
            }
        }
    }
}
