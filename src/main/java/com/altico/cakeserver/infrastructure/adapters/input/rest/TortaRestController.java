package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.mapper.DomainDtoMapper;
import com.altico.cakeserver.applications.ports.input.ImagenServicePort;
import com.altico.cakeserver.applications.ports.input.OcasionServicePort;
import com.altico.cakeserver.applications.ports.input.TortaServicePort;
import com.altico.cakeserver.applications.ports.input.dto.TortaDto;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen.ImagenListResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.OcasionListResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.OcasionResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.mapper.RestDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tortas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tortas", description = "API para gestión de tortas")
public class TortaRestController {
    // Importado para dar soporte a HATEOAS
    private final ImagenServicePort imagenService;
    private final OcasionServicePort ocasionService;

    private final TortaServicePort tortaService;
    private final DomainDtoMapper domainMapper;
    private final RestDtoMapper restMapper;

    @PostMapping
    @Operation(summary = "Crear nueva torta", description = "Crea una nueva torta con sus ocasiones asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Torta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe una torta similar")
    })
    public ResponseEntity<TortaResponse> crear(@Valid @RequestBody CreateTortaRequest request) {
        log.info("Creando nueva torta: {}", request.descripcion());

        var command = restMapper.toCommand(request);
        var torta = tortaService.crear(domainMapper.toDomain(command));

        // Agregar ocasiones si se especificaron
        if (request.ocasionIds() != null && !request.ocasionIds().isEmpty()) {
            for (Integer ocasionId : request.ocasionIds()) {
                tortaService.agregarOcasion(torta.getId(), ocasionId);
            }
            torta = tortaService.obtenerPorId(torta.getId());
        }

        var response = restMapper.toResponse(domainMapper.toDto(torta));
        var location = URI.create("/api/v1/tortas/" + torta.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener torta por ID", description = "Obtiene una torta con todas sus relaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torta encontrada"),
            @ApiResponse(responseCode = "404", description = "Torta no encontrada")
    })
    public ResponseEntity<TortaResponse> obtenerPorId(
            @Parameter(description = "ID de la torta") @PathVariable Integer id) {
        log.info("Obteniendo torta con ID: {}", id);

        var torta = tortaService.obtenerPorId(id);
        var response = restMapper.toResponse(domainMapper.toDto(torta));

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar tortas", description = "Lista todas las tortas con paginación")
    public ResponseEntity<TortaListResponse> listar(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaCreado") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "DESC") String direction) {

        log.info("Listando tortas - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<TortaDto> tortasPage = tortaService.listarPaginado(pageable)
                .map(domainMapper::toDto);

        var response = restMapper.toListTortaResponse(tortasPage);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar torta", description = "Actualiza los datos de una torta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torta actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Torta no encontrada")
    })
    public ResponseEntity<TortaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTortaRequest request) {
        log.info("Actualizando torta con ID: {}", id);

        var command = restMapper.toCommand(request);
        var torta = tortaService.actualizar(id, domainMapper.toDomain(command));
        var response = restMapper.toResponse(domainMapper.toDto(torta));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar torta", description = "Elimina una torta y todas sus relaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Torta eliminada"),
            @ApiResponse(responseCode = "404", description = "Torta no encontrada")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        log.info("Eliminando torta con ID: {}", id);
        tortaService.eliminar(id);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar tortas", description = "Busca tortas por descripción")
    public ResponseEntity<List<TortaSummaryResponse>> buscar(
            @Parameter(description = "Término de búsqueda") @RequestParam String descripcion) {
        log.info("Buscando tortas con descripción: {}", descripcion);

        var tortas = tortaService.buscarPorDescripcion(descripcion);
        var response = tortas.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/ocasiones")
    @Operation(summary = "Agregar ocasión a torta", description = "Asocia una ocasión a una torta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void agregarOcasion(
            @PathVariable Integer id,
            @Valid @RequestBody TortaOcasionRequest request) {
        log.info("Agregando ocasión {} a torta {}", request.ocasionId(), id);
        tortaService.agregarOcasion(id, request.ocasionId());
    }

    @DeleteMapping("/{id}/ocasiones/{ocasionId}")
    @Operation(summary = "Remover ocasión de torta", description = "Desasocia una ocasión de una torta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerOcasion(
            @PathVariable Integer id,
            @PathVariable Integer ocasionId) {
        log.info("Removiendo ocasión {} de torta {}", ocasionId, id);
        tortaService.removerOcasion(id, ocasionId);
    }

    @GetMapping("/ocasion/{ocasionId}")
    @Operation(summary = "Listar tortas por ocasión", description = "Lista todas las tortas asociadas a una ocasión")
    public ResponseEntity<List<TortaSummaryResponse>> listarPorOcasion(@PathVariable Integer ocasionId) {
        log.info("Listando tortas para ocasión ID: {}", ocasionId);

        var tortas = tortaService.buscarPorOcasion(ocasionId);
        var response = tortas.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/imagenes")
    @Operation(summary = "Agregar imagen a torta", description = "Agrega una nueva imagen a la torta")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TortaResponse> agregarImagen(
            @PathVariable Integer id,
            @Valid @RequestBody TortaImagenRequest request) {
        log.info("Agregando imagen a torta {}", id);

        var torta = tortaService.agregarImagen(id, request.url());
        var response = restMapper.toResponse(domainMapper.toDto(torta));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/imagenes/{imagenId}")
    @Operation(summary = "Remover imagen de torta", description = "Elimina una imagen de la torta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerImagen(
            @PathVariable Integer id,
            @PathVariable Integer imagenId) {
        log.info("Removiendo imagen {} de torta {}", imagenId, id);
        tortaService.removerImagen(id, imagenId);
    }

    @GetMapping("/recientes")
    @Operation(summary = "Obtener tortas recientes", description = "Lista las tortas más recientes")
    public ResponseEntity<List<TortaSummaryResponse>> obtenerRecientes(
            @Parameter(description = "Cantidad de tortas") @RequestParam(defaultValue = "10") int cantidad) {
        log.info("Obteniendo {} tortas recientes", cantidad);

        var tortas = tortaService.buscarRecientes(cantidad);
        var response = tortas.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/ocasiones")
    @Operation(summary = "Listar ocasiones de una torta", description = "Lista todas las ocasiones asociadas a una torta")
//    public ResponseEntity<OcasionListResponse> listarOcasionesPorTorta(@PathVariable Integer id) {
    public ResponseEntity<List<OcasionResponse>> listarOcasionesPorTorta(@PathVariable Integer id) {
        log.info("Listando ocasiones para torta ID: {}", id);

        var torta = tortaService.obtenerPorId(id);
        var response = torta.getOcasiones().stream()
                .map(domainMapper::toDto)
                .map(restMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/imagenes")
    @Operation(summary = "Listar imágenes de una torta", description = "Lista todas las imágenes de una torta")
    public ResponseEntity<ImagenListResponse> listarImagenesPorTorta(@PathVariable Integer id) {
        log.info("Listando imágenes para torta ID: {}", id);

        var imagenes = imagenService.listarPorTorta(id);
        var imagenesDto = imagenes.stream()
                .map(domainMapper::toDto)
                .toList();

        return ResponseEntity.ok(restMapper.toListImagenResponse(imagenesDto));
    }
}