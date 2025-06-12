package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.RolPersistencePort;
import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RolPersistenceAdapter implements RolPersistencePort {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolPersistenceMapper rolMapper;
    private final UsuarioPersistenceMapper usuarioMapper;

    @Override
    public RolCompleto save(RolCompleto rol) {
        log.debug("Guardando rol: {}", rol.getNombre());

        RolEntity entity = rolMapper.toEntity(rol);
        RolEntity savedEntity = rolRepository.save(entity);

        return rolMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolCompleto> findById(Integer id) {
        log.debug("Buscando rol por ID: {}", id);

        return rolRepository.findById(id)
                .map(rolMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolCompleto> findByIdWithPermisos(Integer id) {
        log.debug("Buscando rol con permisos por ID: {}", id);

        return rolRepository.findByIdWithPermisos(id)
                .map(rolMapper::toDomainWithPermisos);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolCompleto> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .map(rolMapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        log.debug("Eliminando rol con ID: {}", id);
        rolRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return rolRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolCompleto> findAllWithFilters(Pageable pageable, Boolean activo) {
        log.debug("Buscando roles con filtros - activo: {}", activo);

        return rolRepository.findAllWithFilters(activo, pageable)
                .map(rolMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> findByActivo(boolean activo) {
        return rolRepository.findByActivo(activo).stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> searchByNombreOrDescripcion(String termino) {
        log.debug("Buscando roles por término: {}", termino);

        return rolRepository.searchByNombreOrDescripcion(termino).stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> findAllOrderByPrioridad() {
        return rolRepository.findAllOrderByPrioridad().stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> findWithoutPermisos() {
        return rolRepository.findWithoutPermisos().stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> findWithoutUsers() {
        return rolRepository.findWithoutUsers().stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> findUsersByRolId(Integer rolId) {
        // ✅ CORREGIDO: Buscar el nombre del rol primero y luego los usuarios
        Optional<RolEntity> rolEntity = rolRepository.findById(rolId);
        if (rolEntity.isEmpty()) {
            return List.of();
        }

        String rolNombre = rolEntity.get().getNombre();

        // ✅ CORREGIDO: Usar el método correcto que retorna UsuarioEntity
        return rolRepository.findUsersByRolNombre(rolNombre).stream()
                .map(usuarioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolCompleto> findRolesByUserId(Long usuarioId) {
        return rolRepository.findRolesByUserId(usuarioId).stream()
                .map(rolMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void addPermisoToRol(Integer rolId, Integer permisoId) {
        log.debug("Agregando permiso {} al rol {}", permisoId, rolId);

        RolEntity rol = rolRepository.findByIdWithPermisos(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        PermisoEntity permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));

        rol.agregarPermiso(permiso);
        rolRepository.save(rol);
    }

    @Override
    public void removePermisoFromRol(Integer rolId, Integer permisoId) {
        log.debug("Removiendo permiso {} del rol {}", permisoId, rolId);

        RolEntity rol = rolRepository.findByIdWithPermisos(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        PermisoEntity permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));

        rol.removerPermiso(permiso);
        rolRepository.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean rolHasPermiso(Integer rolId, Integer permisoId) {
        return rolRepository.rolHasPermiso(rolId, permisoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getRolEstadisticas() {
        log.debug("Obteniendo estadísticas de roles");

        List<Object[]> results = rolRepository.getRolEstadisticas();

        Map<String, Long> estadisticas = new HashMap<>();

        for (Object[] row : results) {
            String tipo = (String) row[0];
            Long cantidad = ((Number) row[1]).longValue();
            estadisticas.put(tipo, cantidad);
        }

        // Agregar estadísticas adicionales si no están presentes
        estadisticas.putIfAbsent("total", 0L);
        estadisticas.putIfAbsent("activos", 0L);
        estadisticas.putIfAbsent("inactivos", 0L);
        estadisticas.putIfAbsent("conPermisos", 0L);
        estadisticas.putIfAbsent("sinPermisos", 0L);

        return estadisticas;
    }
}