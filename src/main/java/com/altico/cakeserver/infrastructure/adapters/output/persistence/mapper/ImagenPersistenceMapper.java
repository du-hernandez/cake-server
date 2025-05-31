package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.Imagen;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.ImagenEntity;
import org.springframework.stereotype.Component;

@Component
public class ImagenPersistenceMapper {

    public Imagen toDomain(ImagenEntity entity) {
        if (entity == null) return null;

        Integer tortaId = entity.getTorta() != null ? entity.getTorta().getId() : null;

        return new Imagen(
                entity.getId(),
                entity.getUrl(),
                tortaId,
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    public ImagenEntity toEntity(Imagen domain) {
        if (domain == null) return null;

        ImagenEntity entity = new ImagenEntity();
        entity.setId(domain.getId());
        entity.setUrl(domain.getUrl());
        // La torta se setea en el adapter

        return entity;
    }
}
