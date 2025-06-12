package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.PermisoPersistencePort;
import com.altico.cakeserver.domain.model.Permiso;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.PermisoEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.PermisoPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.PermisoRepository;
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
public class PermisoPersistenceAdapter implements PermisoPersistencePort {

    private final PermisoRepository permisoRepository;
    private final PermisoPersistenceMapper mapper;

    @Override
    public Permiso save(Permiso permiso) {
        log.debug("Guardando permiso: {}", permiso.getNombre());

        PermisoEntity entity = mapper.toEntity(permiso);
        PermisoEntity savedEntity = permisoRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permiso> findById(Integer id) {
        log.debug("Buscando permiso por ID: {}", id);

        return permisoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        log.debug("Eliminando permiso con ID: {}", id);
        permisoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return permisoRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Permiso> findAllWithFilters(Pageable pageable, String recurso, String accion, Boolean activo) {
        log.debug("Buscando permisos con filtros - recurso: {}, accion: {}, activo: {}", recurso, accion, activo);

        return permisoRepository.findAllWithFilters(recurso, accion, activo, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> findAll() {
        return permisoRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> searchByNombreOrDescripcion(String termino) {
        log.debug("Buscando permisos por término: {}", termino);

        return permisoRepository.searchByNombreOrDescripcion(termino).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> findByRecurso(String recurso) {
        return permisoRepository.findByRecurso(recurso).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> findByAccion(String accion) {
        return permisoRepository.findByAccion(accion).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return permisoRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRecursoAndAccion(String recurso, String accion) {
        return permisoRepository.existsByRecursoAndAccion(recurso, accion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permiso> findByRecursoAndAccion(String recurso, String accion) {
        return permisoRepository.findByRecursoAndAccion(recurso, accion)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllRecursos() {
        return permisoRepository.findAllRecursos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllAcciones() {
        return permisoRepository.findAllAcciones();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAccionesByRecurso(String recurso) {
        return permisoRepository.findAccionesByRecurso(recurso);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> getRecursoAccionMatrix() {
        log.debug("Obteniendo matriz de recursos y acciones");

        List<Object[]> results = permisoRepository.getRecursoAccionMatrix();

        return results.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[0], // recurso
                        Collectors.mapping(
                                row -> (String) row[1], // accion
                                Collectors.toList()
                        )
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPermisoInUse(Integer permisoId) {
        return permisoRepository.isPermisoInUse(permisoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permiso> findUnusedPermisos() {
        return permisoRepository.findUnusedPermisos().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPermisoEstadisticas() {
        log.debug("Obteniendo estadísticas de permisos");

        List<Object[]> results = permisoRepository.getPermisoEstadisticas();

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
        estadisticas.putIfAbsent("enUso", 0L);
        estadisticas.putIfAbsent("sinUso", 0L);

        // Calcular estadísticas derivadas
        long totalRecursos = permisoRepository.findAllRecursos().size();
        long totalAcciones = permisoRepository.findAllAcciones().size();

        estadisticas.put("totalRecursos", (long) totalRecursos);
        estadisticas.put("totalAcciones", (long) totalAcciones);

        return estadisticas;
    }
}
