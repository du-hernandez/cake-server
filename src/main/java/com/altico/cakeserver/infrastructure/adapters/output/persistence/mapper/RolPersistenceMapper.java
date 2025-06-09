package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.domain.model.Permiso;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RolEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolPersistenceMapper {

    private final PermisoPersistenceMapper permisoMapper;

    /**
     * Convierte una entidad de persistencia a modelo de dominio (sin permisos)
     */
    public RolCompleto toDomain(RolEntity entity) {
        if (entity == null) return null;

        return new RolCompleto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrioridad(),
                entity.isActivo(),
                new HashSet<>(), // Sin permisos para evitar lazy loading
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    /**
     * Convierte una entidad de persistencia a modelo de dominio (con permisos)
     * Usar solo cuando los permisos han sido cargados con FETCH JOIN
     */
    public RolCompleto toDomainWithPermisos(RolEntity entity) {
        if (entity == null) return null;

        Set<Permiso> permisos = entity.getPermisos() != null
                ? entity.getPermisos().stream()
                .map(permisoMapper::toDomain)
                .collect(Collectors.toSet())
                : new HashSet<>();

        return new RolCompleto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrioridad(),
                entity.isActivo(),
                permisos,
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    /**
     * Convierte un modelo de dominio a entidad de persistencia
     */
    public RolEntity toEntity(RolCompleto domain) {
        if (domain == null) return null;

        RolEntity entity = new RolEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setPrioridad(domain.getPrioridad());
        entity.setActivo(domain.isActivo());

        // Las fechas se manejan automáticamente con @CreationTimestamp y @UpdateTimestamp
        // Solo se setean si vienen del dominio (para actualizaciones)
        if (domain.getFechaCreado() != null) {
            entity.setFechaCreado(domain.getFechaCreado());
        }
        if (domain.getFechaActualizado() != null) {
            entity.setFechaActualizado(domain.getFechaActualizado());
        }

        // Los permisos se manejan por separado en las relaciones
        // No los mapeamos aquí para evitar problemas de sincronización

        return entity;
    }

    /**
     * Actualiza una entidad existente con los datos del dominio
     * Útil para operaciones de actualización
     */
    public RolEntity updateEntity(RolEntity entity, RolCompleto domain) {
        if (entity == null || domain == null) return entity;

        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setPrioridad(domain.getPrioridad());
        entity.setActivo(domain.isActivo());

        // Las fechas de actualización se manejan automáticamente
        // Los permisos se actualizan por separado

        return entity;
    }

    /**
     * Convierte entidad a dominio preservando solo la información básica
     * Útil para listados donde no se necesitan los permisos
     */
    public RolCompleto toDomainBasic(RolEntity entity) {
        return toDomain(entity); // Misma implementación por ahora
    }
}