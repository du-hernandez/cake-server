package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.ports.input.PermisoServicePort;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.mapper.AdminDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/permisos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Permisos", description = "API para gestión de permisos del sistema")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPermisoController {

    private final PermisoServicePort permisoService;
    private final AdminDtoMapper adminMapper;

    @PostMapping
    @Operation(summary = "Crear permiso", description = "Crea un nuevo permiso en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permiso creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Permiso ya existe")
    })
    public ResponseEntity<PermisoResponse> crearPermiso(@Valid @RequestBody CreatePermisoRequest request) {
        log.info("Creando permiso: {} para recurso: {}", request.nombre(), request.recurso());

        var command = adminMapper.toCommand(request);
        var permiso = permisoService.crearPermiso(command);
        var response = adminMapper.toResponse(permiso);

        var location = URI.create("/api/v1/admin/permisos/" + permiso.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar permisos", description = "Lista todos los permisos con paginación")
    public ResponseEntity<PermisoListResponse> listarPermisos(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "nombre") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String direction,
            @Parameter(description = "Filtro por recurso") @RequestParam(required = false) String recurso,
            @Parameter(description = "Filtro por acción") @RequestParam(required = false) String accion,
            @Parameter(description = "Filtro por estado") @RequestParam(required = false) Boolean activo) {

        log.info("Listando permisos - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        var permisosPage = permisoService.listarPermisos(pageable, recurso, accion, activo);
        var response = adminMapper.toPermisoListResponse(permisosPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener permiso", description = "Obtiene un permiso específico por ID")
    public ResponseEntity<PermisoResponse> obtenerPermiso(@PathVariable Integer id) {
        log.info("Obteniendo permiso con ID: {}", id);

        var permiso = permisoService.obtenerPorId(id);
        var response = adminMapper.toResponse(permiso);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar permiso", description = "Actualiza un permiso existente")
    public ResponseEntity<PermisoResponse> actualizarPermiso(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePermisoRequest request) {
        log.info("Actualizando permiso con ID: {}", id);

        var command = adminMapper.toUpdateCommand(request);
        var permiso = permisoService.actualizarPermiso(id, command);
        var response = adminMapper.toResponse(permiso);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de permiso", description = "Activa o desactiva un permiso")
    public ResponseEntity<PermisoResponse> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody ChangePermisoStatusRequest request) {
        log.info("Cambiando estado de permiso {} a: {}", id, request.activo());

        var permiso = request.activo()
                ? permisoService.activarPermiso(id)
                : permisoService.desactivarPermiso(id);
        var response = adminMapper.toResponse(permiso);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar permiso", description = "Elimina un permiso del sistema")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPermiso(@PathVariable Integer id) {
        log.info("Eliminando permiso con ID: {}", id);
        permisoService.eliminarPermiso(id);
    }

    @GetMapping("/recursos")
    @Operation(summary = "Listar recursos", description = "Lista todos los recursos disponibles")
    public ResponseEntity<List<String>> listarRecursos() {
        log.info("Listando recursos disponibles");

        var recursos = permisoService.listarRecursos();
        return ResponseEntity.ok(recursos);
    }

    @GetMapping("/acciones/{recurso}")
    @Operation(summary = "Listar acciones por recurso", description = "Lista todas las acciones disponibles para un recurso")
    public ResponseEntity<List<String>> listarAccionesPorRecurso(@PathVariable String recurso) {
        log.info("Listando acciones para recurso: {}", recurso);

        var acciones = permisoService.listarAccionesPorRecurso(recurso);
        return ResponseEntity.ok(acciones);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar permisos", description = "Busca permisos por criterios")
    public ResponseEntity<List<PermisoResponse>> buscarPermisos(
            @Parameter(description = "Término de búsqueda") @RequestParam String q) {
        log.info("Buscando permisos con término: {}", q);

        var permisos = permisoService.buscarPermisos(q);
        var response = permisos.stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
