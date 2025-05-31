package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TortaPersistenceMapper {

    private final OcasionPersistenceMapper ocasionMapper;
    private final ImagenPersistenceMapper imagenMapper;

    public TortaPersistenceMapper(OcasionPersistenceMapper ocasionMapper,
                                  ImagenPersistenceMapper imagenMapper) {
        this.ocasionMapper = ocasionMapper;
        this.imagenMapper = imagenMapper;
    }

    public Torta toDomain(TortaEntity entity) {
        if (entity == null) return null;

        return new Torta(
                entity.getId(),
                entity.getDescripcion(),
                entity.getImagen(),
                entity.getFechaCreado(),
                entity.getFechaActualizado(),
                Collections.emptySet(), // Sin relaciones
                Collections.emptySet()  // Sin relaciones
        );
    }

    public Torta toDomainWithRelations(TortaEntity entity) {
        if (entity == null) return null;

        Set<Ocasion> ocasiones = entity.getTortaOcasiones().stream()
                .filter(to -> to.getEstado() == 1)
                .map(to -> ocasionMapper.toDomain(to.getOcasion()))
                .collect(Collectors.toSet());

        Set<Imagen> imagenes = entity.getImagenes().stream()
                .map(imagenMapper::toDomain)
                .collect(Collectors.toSet());

        return new Torta(
                entity.getId(),
                entity.getDescripcion(),
                entity.getImagen(),
                entity.getFechaCreado(),
                entity.getFechaActualizado(),
                ocasiones,
                imagenes
        );
    }

    public TortaEntity toEntity(Torta domain) {
        if (domain == null) return null;

        TortaEntity entity = new TortaEntity();
        entity.setId(domain.getId());
        entity.setDescripcion(domain.getDescripcion());
        entity.setImagen(domain.getImagen());

        // Las fechas se manejan automáticamente con @CreationTimestamp y @UpdateTimestamp

        return entity;
    }

    public TortaEntity updateEntity(TortaEntity entity, Torta domain) {
        if (entity == null || domain == null) return entity;

        entity.setDescripcion(domain.getDescripcion());
        entity.setImagen(domain.getImagen());

        return entity;
    }
}
