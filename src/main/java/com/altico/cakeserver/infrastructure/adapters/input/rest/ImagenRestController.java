package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.mapper.DomainDtoMapper;
import com.altico.cakeserver.applications.ports.input.ImagenServicePort;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.imagen.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.mapper.RestDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/imagenes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Imágenes", description = "API para gestión de imágenes")
public class ImagenRestController {

    private final ImagenServicePort imagenService;
    private final DomainDtoMapper domainMapper;
    private final RestDtoMapper restMapper;

    @PostMapping
    @Operation(summary = "Crear nueva imagen", description = "Agrega una nueva imagen asociada a una torta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imagen creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Torta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una imagen con esa URL")
    })
    public ResponseEntity<ImagenResponse> crear(@Valid @RequestBody CreateImagenRequest request) {
        log.info("Creando nueva imagen para torta: {}", request.tortaId());

        var command = restMapper.toCommand(request);
        var imagen = imagenService.crear(domainMapper.toDomain(command));
        var response = restMapper.toResponse(domainMapper.toDto(imagen));

        var location = URI.create("/api/v1/imagenes/" + imagen.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener imagen por ID", description = "Obtiene una imagen específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen encontrada"),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    })
    public ResponseEntity<ImagenResponse> obtenerPorId(
            @Parameter(description = "ID de la imagen") @PathVariable Integer id) {
        log.info("Obteniendo imagen con ID: {}", id);

        var imagen = imagenService.obtenerPorId(id);
        var response = restMapper.toResponse(domainMapper.toDto(imagen));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/torta/{tortaId}")
    @Operation(summary = "Listar imágenes por torta", description = "Lista todas las imágenes de una torta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de imágenes"),
            @ApiResponse(responseCode = "404", description = "Torta no encontrada")
    })
    public ResponseEntity<ImagenListResponse> listarPorTorta(
            @Parameter(description = "ID de la torta") @PathVariable Integer tortaId) {
        log.info("Listando imágenes de torta: {}", tortaId);

        var imagenes = imagenService.listarPorTorta(tortaId);
        var imagenesDto = imagenes.stream()
                .map(domainMapper::toDto)
                .toList();

        var response = restMapper.toListImagenResponse(imagenesDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imagen eliminada"),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        log.info("Eliminando imagen con ID: {}", id);
        imagenService.eliminar(id);
    }

    @DeleteMapping("/torta/{tortaId}")
    @Operation(summary = "Eliminar todas las imágenes de una torta",
            description = "Elimina todas las imágenes asociadas a una torta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imágenes eliminadas"),
            @ApiResponse(responseCode = "404", description = "Torta no encontrada")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPorTorta(@PathVariable Integer tortaId) {
        log.info("Eliminando todas las imágenes de torta: {}", tortaId);
        imagenService.eliminarPorTorta(tortaId);
    }

    @GetMapping
    @Operation(summary = "Listar todas las imágenes", description = "Lista todas las imágenes del sistema")
    public ResponseEntity<ImagenListResponse> listarTodas() {
        log.info("Listando todas las imágenes");

        var imagenes = imagenService.listarTodas();
        var imagenesDto = imagenes.stream()
                .map(domainMapper::toDto)
                .toList();

        var response = restMapper.toListImagenResponse(imagenesDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/limpiar-huerfanas")
    @Operation(summary = "Limpiar imágenes huérfanas",
            description = "Elimina imágenes que no están asociadas a ninguna torta")
    public ResponseEntity<Map<String, Object>> limpiarHuerfanas() {
        log.info("Limpiando imágenes huérfanas");

        int eliminadas = imagenService.limpiarImagenesHuerfanas();

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Limpieza completada");
        response.put("imagenesEliminadas", eliminadas);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar-url")
    @Operation(summary = "Verificar disponibilidad de URL",
            description = "Verifica si una URL ya está siendo utilizada")
    public ResponseEntity<Map<String, Boolean>> verificarUrl(
            @Parameter(description = "URL a verificar") @RequestParam String url) {
        log.info("Verificando disponibilidad de URL: {}", url);

        boolean existe = imagenService.existePorUrl(url);

        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", existe);
        response.put("disponible", !existe);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas/torta/{tortaId}")
    @Operation(summary = "Estadísticas de imágenes por torta",
            description = "Obtiene estadísticas de imágenes para una torta")
    public ResponseEntity<EstadisticasImagenResponse> obtenerEstadisticasPorTorta(
            @PathVariable Integer tortaId) {
        log.info("Obteniendo estadísticas de imágenes para torta: {}", tortaId);

        long cantidad = imagenService.contarPorTorta(tortaId);

        var response = new EstadisticasImagenResponse(
                tortaId,
                cantidad,
                10 - cantidad, // Asumiendo un máximo de 10 imágenes
                cantidad >= 10
        );

        return ResponseEntity.ok(response);
    }

    // DTO para estadísticas
    public record EstadisticasImagenResponse(
            Integer tortaId,
            long cantidadImagenes,
            long espacioDisponible,
            boolean limiteAlcanzado
    ) {}
}
