package com.altico.cakeserver.applications.mapper;

import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DomainDtoMapper {

    // Mappers para Torta
    public Torta toDomain(CreateTortaCommand command) {
        return Torta.crear(command.descripcion(), command.imagen());
    }
    public Torta toDomain(UpdateTortaCommand command) {
        return Torta.crear(command.descripcion(), command.imagen());
    }

    public TortaDto toDto(Torta torta) {
        return new TortaDto(
                torta.getId(),
                torta.getDescripcion(),
                torta.getImagen(),
                torta.getFechaCreado(),
                torta.getFechaActualizado(),
                torta.getOcasiones().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet()),
                torta.getImagenes().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet())
        );
    }

    // Mappers para Ocasion
    public Ocasion toDomain(CreateOcasionCommand command) {
        return Ocasion.crear(command.nombre());
    }

    public OcasionDto toDto(Ocasion ocasion) {
        return new OcasionDto(
                ocasion.getId(),
                ocasion.getNombre(),
                ocasion.estaActiva(),
                ocasion.getFechaCreado(),
                ocasion.getFechaActualizado()
        );
    }

    // Mappers para Imagen
    public Imagen toDomain(CreateImagenCommand command) {
        return Imagen.crear(command.url(), command.tortaId());
    }

    public ImagenDto toDto(Imagen imagen) {
        return new ImagenDto(
                imagen.getId(),
                imagen.getUrl(),
                imagen.getTortaId(),
                imagen.getFechaCreado(),
                imagen.getFechaActualizado()
        );
    }

    // Mappers para listas
    public Set<TortaDto> toTortaDtoSet(Set<Torta> tortas) {
        if (tortas == null) return Collections.emptySet();
        return tortas.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    public Set<OcasionDto> toOcasionDtoSet(Set<Ocasion> ocasiones) {
        if (ocasiones == null) return Collections.emptySet();
        return ocasiones.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    public Set<ImagenDto> toImagenDtoSet(Set<Imagen> imagenes) {
        if (imagenes == null) return Collections.emptySet();
        return imagenes.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
