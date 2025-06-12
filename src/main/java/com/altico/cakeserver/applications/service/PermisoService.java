package com.altico.cakeserver.applications.service;

import com.altico.cakeserver.applications.ports.input.PermisoServicePort;
import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.applications.ports.output.PermisoPersistencePort;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.Permiso;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PermisoService implements PermisoServicePort {

    private final PermisoPersistencePort permisoPersistence;

    @Override
    public Permiso crearPermiso(CreatePermisoCommand command) {
        log.info("Creando permiso: {} para {}:{}", command.nombre(), command.recurso(), command.accion());

        // Validar duplicados
        if (permisoPersistence.existsByNombre(command.nombre())) {
            throw new DuplicatePermissionException("Ya existe un permiso con el nombre: " + command.nombre());
        }

        if (permisoPersistence.existsByRecursoAndAccion(command.recurso(), command.accion())) {
            throw new DuplicatePermissionException(
                    "Ya existe un permiso para el recurso: " + command.recurso() + " y acción: " + command.accion());
        }

        Permiso permiso = Permiso.crear(command.nombre(), command.descripcion(),
                command.recurso(), command.accion());

        return permisoPersistence.save(permiso);
    }

    @Override
    @Transactional(readOnly = true)
    public Permiso obtenerPorId(Integer id) {
        return permisoPersistence.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException(id));
    }

    @Override
    public Permiso actualizarPermiso(Integer id, UpdatePermisoCommand command) {
        log.info("Actualizando permiso con ID: {}", id);

        Permiso permiso = obtenerPorId(id);

        // Validar nombre único si cambió
        if (command.nombre() != null && !command.nombre().equals(permiso.getNombre())) {
            if (permisoPersistence.existsByNombre(command.nombre())) {
                throw new DuplicatePermissionException("Ya existe un permiso con el nombre: " + command.nombre());
            }
        }

        Permiso permisoActualizado = new Permiso(
                permiso.getId(),
                command.nombre() != null ? command.nombre() : permiso.getNombre(),
                command.descripcion() != null ? command.descripcion() : permiso.getDescripcion(),
                permiso.getRecurso(),
                permiso.getAccion(),
                permiso.isActivo(),
                permiso.getFechaCreado(),
                java.time.LocalDateTime.now()
        );

        return permisoPersistence.save(permisoActualizado);
    }

    @Override
    public void eliminarPermiso(Integer id) {
        log.info("Eliminando permiso con ID: {}", id);

        if (!permisoPersistence.existsById(id)) {
            throw new PermissionNotFoundException(id);
        }

        // Verificar si está en uso
        if (permisoPersistence.isPermisoInUse(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar un permiso que está en uso");
        }

        permisoPersistence.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Permiso> listarPermisos(Pageable pageable, String recurso, String accion, Boolean activo) {
        return permisoPersistence.findAllWithFilters(pageable, recurso, accion, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> buscarPermisos(String termino) {
        return permisoPersistence.searchByNombreOrDescripcion(termino);
    }

    @Override
    public Permiso activarPermiso(Integer id) {
        Permiso permiso = obtenerPorId(id);
        Permiso permisoActivado = permiso.activar();
        return permisoPersistence.save(permisoActivado);
    }

    @Override
    public Permiso desactivarPermiso(Integer id) {
        Permiso permiso = obtenerPorId(id);
        Permiso permisoDesactivado = permiso.desactivar();
        return permisoPersistence.save(permisoDesactivado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarRecursos() {
        return permisoPersistence.findAllRecursos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarAcciones() {
        return permisoPersistence.findAllAcciones();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarAccionesPorRecurso(String recurso) {
        return permisoPersistence.findAccionesByRecurso(recurso);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> obtenerMatrizRecursosAcciones() {
        return permisoPersistence.getRecursoAccionMatrix();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePermiso(String recurso, String accion) {
        return permisoPersistence.existsByRecursoAndAccion(recurso, accion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return permisoPersistence.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Permiso obtenerPorRecursoYAccion(String recurso, String accion) {
        return permisoPersistence.findByRecursoAndAccion(recurso, accion)
                .orElseThrow(() -> new PermissionNotFoundException(
                        "Permiso no encontrado para recurso: " + recurso + " y acción: " + accion));
    }

    @Override
    public List<Permiso> sincronizarPermisosSistema() {
        log.info("Sincronizando permisos del sistema");

        // Obtener permisos del sistema mediante reflection de anotaciones @PreAuthorize
        List<PermisoSistema> permisosSistema = extraerPermisosDeSistema();

        List<Permiso> permisosCreados = permisosSistema.stream()
                .filter(ps -> !existePermiso(ps.recurso(), ps.accion()))
                .map(ps -> crearPermiso(new CreatePermisoCommand(
                        ps.nombre(), ps.descripcion(), ps.recurso(), ps.accion())))
                .collect(Collectors.toList());

        log.info("Creados {} permisos del sistema", permisosCreados.size());
        return permisosCreados;
    }

    @Override
    public void crearPermisosSistemaBasicos() {
        crearPermisoBasicoSiNoExiste("Crear Torta", "Permite crear tortas", "tortas", "create");
        crearPermisoBasicoSiNoExiste("Leer Torta", "Permite ver tortas", "tortas", "read");
        crearPermisoBasicoSiNoExiste("Actualizar Torta", "Permite actualizar tortas", "tortas", "update");
        crearPermisoBasicoSiNoExiste("Eliminar Torta", "Permite eliminar tortas", "tortas", "delete");

        crearPermisoBasicoSiNoExiste("Gestionar Usuarios", "Administración de usuarios", "usuarios", "manage");
        crearPermisoBasicoSiNoExiste("Gestionar Roles", "Administración de roles", "roles", "manage");
        crearPermisoBasicoSiNoExiste("Gestionar Permisos", "Administración de permisos", "permisos", "manage");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticasPermisos() {
        return permisoPersistence.getPermisoEstadisticas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> obtenerPermisosNoUtilizados() {
        return permisoPersistence.findUnusedPermisos();
    }

    // Métodos auxiliares
    private void crearPermisoBasicoSiNoExiste(String nombre, String descripcion, String recurso, String accion) {
        if (!existePermiso(recurso, accion)) {
            crearPermiso(new CreatePermisoCommand(nombre, descripcion, recurso, accion));
        }
    }

    private List<PermisoSistema> extraerPermisosDeSistema() {
        // Implementar extracción de permisos mediante reflection
        // Esta implementación analizaría las anotaciones @PreAuthorize en los controladores
        return List.of(); // Placeholder
    }

    // Clase auxiliar
    private record PermisoSistema(String nombre, String descripcion, String recurso, String accion) {}

    @Override
    public List<Permiso> obtenerPermisosPorRecurso(String recurso) {
        return permisoPersistence.findByRecurso(recurso);
    }

    @Override
    public List<Permiso> obtenerPermisosPorAccion(String accion) {
        return permisoPersistence.findByAccion(accion);
    }
}
