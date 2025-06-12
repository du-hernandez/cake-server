package com.altico.cakeserver.infrastructure.adapters.input.rest.mapper;

import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen.CreateImagenRequest;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen.ImagenListResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen.ImagenResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.CreateOcasionRequest;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.OcasionListResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.OcasionResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.stream.Collectors;

@Component
public class RestDtoMapper {

    // Mappers para Torta
    public CreateTortaCommand toCommand(CreateTortaRequest request) {
        return new CreateTortaCommand(
                request.descripcion(),
                request.imagenPrincipal(),
                request.ocasionIds()
        );
    }

    public UpdateTortaCommand toCommand(UpdateTortaRequest request) {
        return new UpdateTortaCommand(
                request.descripcion(),
                request.imagenPrincipal()
        );
    }

    public TortaResponse toResponse(TortaDto dto) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return new TortaResponse(
                dto.id(),
                dto.descripcion(),
                dto.imagen(),
                dto.fechaCreado(),
                dto.fechaActualizado(),
                dto.ocasiones().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toSet()),
                dto.imagenes().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toSet()),
                new TortaResponse.Links(
                        baseUrl + "/api/v1/tortas/" + dto.id(),
                        baseUrl + "/api/v1/tortas/" + dto.id() + "/ocasiones",
                        baseUrl + "/api/v1/tortas/" + dto.id() + "/imagenes"
                )
        );
    }

    public TortaSummaryResponse toSummaryResponse(TortaDto dto) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return new TortaSummaryResponse(
                dto.id(),
                dto.descripcion(),
                dto.imagen(),
                dto.fechaCreado(),
                dto.ocasiones().size(),
                dto.imagenes().size(),
                baseUrl + "/api/v1/tortas/" + dto.id()
        );
    }

    public TortaListResponse toListTortaResponse(Page<TortaDto> page) {
        return new TortaListResponse(
                page.getContent().stream()
                        .map(this::toSummaryResponse)
                        .collect(Collectors.toList()),
                new TortaListResponse.PageMetadata(
                        page.getSize(),
                        (int) page.getTotalElements(),
                        page.getTotalPages(),
                        page.getNumber()
                )
        );
    }

    // Mappers para Ocasion
    public CreateOcasionCommand toCommand(CreateOcasionRequest request) {
        return new CreateOcasionCommand(request.nombre());
    }

    public OcasionResponse toResponse(OcasionDto dto) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return new OcasionResponse(
                dto.id(),
                dto.nombre(),
                dto.activo(),
                dto.fechaCreado(),
                dto.fechaActualizado(),
                new OcasionResponse.Links(
                        baseUrl + "/api/v1/ocasiones/" + dto.id(),
                        baseUrl + "/api/v1/ocasiones/" + dto.id() + "/tortas"
                )
        );
    }

    public OcasionListResponse toListOcasionResponse(Page<OcasionDto> page) {
        return new OcasionListResponse(
                page.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                new OcasionListResponse.PageMetadata(
                        page.getSize(),
                        (int) page.getTotalElements(),
                        page.getTotalPages(),
                        page.getNumber()
                )
        );
    }

    // Mappers para Imagen
    public CreateImagenCommand toCommand(CreateImagenRequest request) {
        return new CreateImagenCommand(request.url(), request.tortaId());
    }

    public ImagenResponse toResponse(ImagenDto dto) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return new ImagenResponse(
                dto.id(),
                dto.url(),
                dto.tortaId(),
                dto.fechaCreado(),
                dto.fechaActualizado(),
                new ImagenResponse.Links(
                        baseUrl + "/api/v1/imagenes/" + dto.id(),
                        baseUrl + "/api/v1/tortas/" + dto.tortaId()
                )
        );
    }

    public ImagenListResponse toListImagenResponse(java.util.List<ImagenDto> imagenes) {
        return new ImagenListResponse(
                imagenes.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                imagenes.size()
        );
    }
}