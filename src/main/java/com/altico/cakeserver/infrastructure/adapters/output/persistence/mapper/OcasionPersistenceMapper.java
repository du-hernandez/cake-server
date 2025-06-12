package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.Ocasion;
import com.altico.cakeserver.domain.model.EstadoOcasion;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.OcasionEntity;
import org.springframework.stereotype.Component;

@Component
public class OcasionPersistenceMapper {

    public Ocasion toDomain(OcasionEntity entity) {
        if (entity == null) return null;

        EstadoOcasion estado = EstadoOcasion.fromValor(entity.getEstado());

        return new Ocasion(
                entity.getId(),
                entity.getNombre(),
                estado,
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    public OcasionEntity toEntity(Ocasion domain) {
        if (domain == null) return null;

        OcasionEntity entity = new OcasionEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setEstado((byte) domain.getEstado().getValor());

        return entity;
    }
}
