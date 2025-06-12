package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.Permiso;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.PermisoEntity;
import org.springframework.stereotype.Component;

@Component
public class PermisoPersistenceMapper {

    /**
     * Convierte una entidad de persistencia a modelo de dominio
     */
    public Permiso toDomain(PermisoEntity entity) {
        if (entity == null) return null;

        return new Permiso(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getRecurso(),
                entity.getAccion(),
                entity.isActivo(),
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    /**
     * Convierte un modelo de dominio a entidad de persistencia
     */
    public PermisoEntity toEntity(Permiso domain) {
        if (domain == null) return null;

        PermisoEntity entity = new PermisoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setRecurso(domain.getRecurso());
        entity.setAccion(domain.getAccion());
        entity.setActivo(domain.isActivo());

        // Las fechas se manejan automáticamente con @CreationTimestamp y @UpdateTimestamp
        // Solo se setean si vienen del dominio (para actualizaciones)
        if (domain.getFechaCreado() != null) {
            entity.setFechaCreado(domain.getFechaCreado());
        }
        if (domain.getFechaActualizado() != null) {
            entity.setFechaActualizado(domain.getFechaActualizado());
        }

        return entity;
    }

    /**
     * Actualiza una entidad existente con los datos del dominio
     * Útil para operaciones de actualización
     */
    public PermisoEntity updateEntity(PermisoEntity entity, Permiso domain) {
        if (entity == null || domain == null) return entity;

        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setRecurso(domain.getRecurso());
        entity.setAccion(domain.getAccion());
        entity.setActivo(domain.isActivo());

        // Las fechas de actualización se manejan automáticamente
        return entity;
    }
}
