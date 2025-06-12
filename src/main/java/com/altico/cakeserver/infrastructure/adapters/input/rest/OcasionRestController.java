package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.mapper.DomainDtoMapper;
import com.altico.cakeserver.applications.ports.input.OcasionServicePort;
import com.altico.cakeserver.applications.ports.input.TortaServicePort;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.ocasion.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.torta.TortaSummaryResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.mapper.RestDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ocasiones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ocasiones", description = "API para gestión de ocasiones")
public class OcasionRestController {

    // Importado para dar soporte a HATEOAS
    private final TortaServicePort tortaService;

    private final OcasionServicePort ocasionService;
    private final DomainDtoMapper domainMapper;
    private final RestDtoMapper restMapper;

    @PostMapping
    @Operation(summary = "Crear nueva ocasión", description = "Crea una nueva ocasión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ocasión creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe una ocasión con ese nombre")
    })
    public ResponseEntity<OcasionResponse> crear(@Valid @RequestBody CreateOcasionRequest request) {
        log.info("Creando nueva ocasión: {}", request.nombre());

        var command = restMapper.toCommand(request);
        var ocasion = ocasionService.crear(domainMapper.toDomain(command));
        var response = restMapper.toResponse(domainMapper.toDto(ocasion));

        var location = URI.create("/api/v1/ocasiones/" + ocasion.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ocasión por ID", description = "Obtiene una ocasión específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ocasión encontrada"),
            @ApiResponse(responseCode = "404", description = "Ocasión no encontrada")
    })
    public ResponseEntity<OcasionResponse> obtenerPorId(
            @Parameter(description = "ID de la ocasión") @PathVariable Integer id) {
        log.info("Obteniendo ocasión con ID: {}", id);

        var ocasion = ocasionService.obtenerPorId(id);
        var response = restMapper.toResponse(domainMapper.toDto(ocasion));

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar ocasiones", description = "Lista todas las ocasiones con paginación")
    public ResponseEntity<OcasionListResponse> listar(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "nombre") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String direction) {

        log.info("Listando ocasiones - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        var ocasionesPage = ocasionService.listarPaginado(pageable)
                .map(domainMapper::toDto);

        var response = restMapper.toListOcasionResponse(ocasionesPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar ocasiones activas", description = "Lista solo las ocasiones activas")
    public ResponseEntity<List<OcasionResponse>> listarActivas() {
        log.info("Listando ocasiones activas");

        var ocasiones = ocasionService.listarActivas();
        var response = ocasiones.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ocasión", description = "Actualiza el nombre de una ocasión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ocasión actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Ocasión no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una ocasión con ese nombre")
    })
    public ResponseEntity<OcasionResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateOcasionRequest request) {
        log.info("Actualizando ocasión con ID: {}", id);

        var ocasion = ocasionService.obtenerPorId(id);
        var ocasionActualizada = ocasion.actualizarNombre(request.nombre());
        ocasionActualizada = ocasionService.actualizar(id, ocasionActualizada);

        var response = restMapper.toResponse(domainMapper.toDto(ocasionActualizada));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ocasión", description = "Elimina una ocasión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ocasión eliminada"),
            @ApiResponse(responseCode = "404", description = "Ocasión no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar, tiene tortas asociadas")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        log.info("Eliminando ocasión con ID: {}", id);
        ocasionService.eliminar(id);
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar ocasión", description = "Activa una ocasión desactivada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ocasión activada"),
            @ApiResponse(responseCode = "404", description = "Ocasión no encontrada")
    })
    public ResponseEntity<OcasionResponse> activar(@PathVariable Integer id) {
        log.info("Activando ocasión con ID: {}", id);

        var ocasion = ocasionService.activar(id);
        var response = restMapper.toResponse(domainMapper.toDto(ocasion));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar ocasión", description = "Desactiva una ocasión activa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ocasión desactivada"),
            @ApiResponse(responseCode = "404", description = "Ocasión no encontrada")
    })
    public ResponseEntity<OcasionResponse> desactivar(@PathVariable Integer id) {
        log.info("Desactivando ocasión con ID: {}", id);

        var ocasion = ocasionService.desactivar(id);
        var response = restMapper.toResponse(domainMapper.toDto(ocasion));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar ocasiones", description = "Busca ocasiones por nombre")
    public ResponseEntity<List<OcasionResponse>> buscar(
            @Parameter(description = "Término de búsqueda") @RequestParam String nombre) {
        log.info("Buscando ocasiones con nombre: {}", nombre);

        var ocasiones = ocasionService.buscarPorNombre(nombre);
        var response = ocasiones.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/populares")
    @Operation(summary = "Obtener ocasiones populares", description = "Lista las ocasiones más populares por cantidad de tortas")
    public ResponseEntity<List<OcasionEstadisticaResponse>> obtenerMasPopulares(
            @Parameter(description = "Cantidad de ocasiones") @RequestParam(defaultValue = "5") int cantidad) {
        log.info("Obteniendo {} ocasiones más populares", cantidad);

        var estadisticas = ocasionService.obtenerOcasionesMasPopulares(cantidad);
        var response = estadisticas.stream()
                .map(stat -> new OcasionEstadisticaResponse(
                        (Integer) stat.get("id"),
                        (String) stat.get("nombre"),
                        ((Number) stat.get("cantidadTortas")).longValue(),
                        true
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sin-tortas")
    @Operation(summary = "Obtener ocasiones sin tortas", description = "Lista las ocasiones que no tienen tortas asociadas")
    public ResponseEntity<List<OcasionResponse>> obtenerSinTortas() {
        log.info("Obteniendo ocasiones sin tortas");

        var ocasiones = ocasionService.obtenerOcasionesSinTortas();
        var response = ocasiones.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas", description = "Obtiene estadísticas generales de ocasiones")
    public ResponseEntity<EstadisticasOcasionResponse> obtenerEstadisticas() {
        log.info("Obteniendo estadísticas de ocasiones");

        var totalOcasiones = ocasionService.listarTodas().size();
        var ocasionesActivas = ocasionService.contarActivas();
        var ocasionesSinTortas = ocasionService.obtenerOcasionesSinTortas().size();

        var response = new EstadisticasOcasionResponse(
                totalOcasiones,
                ocasionesActivas,
                totalOcasiones - ocasionesActivas,
                ocasionesSinTortas
        );

        return ResponseEntity.ok(response);
    }

    //Soporte HATEOAS
    @GetMapping("/{id}/tortas")
    @Operation(summary = "Listar tortas de una ocasión", description = "Lista todas las tortas asociadas a una ocasión específica")
    public ResponseEntity<List<TortaSummaryResponse>> listarTortasPorOcasion(@PathVariable Integer id) {
        log.info("Listando tortas para ocasión ID: {}", id);

        // Verificar que la ocasión existe
        ocasionService.obtenerPorId(id);

        // Usar el servicio de tortas para buscar por ocasión
        var tortas = tortaService.buscarPorOcasion(id);
        var response = tortas.stream()
                .map(domainMapper::toDto)
                .map(restMapper::toSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // DTO para estadísticas
    public record EstadisticasOcasionResponse(
            long total,
            long activas,
            long inactivas,
            long sinTortas
    ) {}
}