package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.ports.input.RolServicePort;
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
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Roles", description = "API para gestión de roles del sistema")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRolController {

    private final RolServicePort rolService;
    private final AdminDtoMapper adminMapper;

    @PostMapping
    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Rol ya existe")
    })
    public ResponseEntity<RolResponse> crearRol(@Valid @RequestBody CreateRolRequest request) {
        log.info("Creando rol: {}", request.nombre());

        var command = adminMapper.toCommand(request);
        var rol = rolService.crearRol(command);
        var response = adminMapper.toResponse(rol);

        var location = URI.create("/api/v1/admin/roles/" + rol.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar roles", description = "Lista todos los roles con paginación")
    public ResponseEntity<RolListResponse> listarRoles(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "prioridad") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String direction,
            @Parameter(description = "Filtro por estado") @RequestParam(required = false) Boolean activo) {

        log.info("Listando roles - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        var rolesPage = rolService.listarRoles(pageable, activo);
        var response = adminMapper.toRolListResponse(rolesPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol", description = "Obtiene un rol específico con sus permisos")
    public ResponseEntity<RolDetailResponse> obtenerRol(@PathVariable Integer id) {
        log.info("Obteniendo rol con ID: {}", id);

        var rol = rolService.obtenerPorIdConPermisos(id);
        var response = adminMapper.toDetailResponse(rol);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol", description = "Actualiza un rol existente")
    public ResponseEntity<RolResponse> actualizarRol(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRolRequest request) {
        log.info("Actualizando rol con ID: {}", id);

        var command = adminMapper.toUpdateCommand(request);
        var rol = rolService.actualizarRol(id, command);
        var response = adminMapper.toResponse(rol);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/permisos")
    @Operation(summary = "Asignar permiso a rol", description = "Agrega un permiso a un rol")
    public ResponseEntity<RolDetailResponse> asignarPermiso(
            @PathVariable Integer id,
            @Valid @RequestBody AssignPermisoToRolRequest request) {
        log.info("Asignando permiso {} al rol {}", request.permisoId(), id);

        var rol = rolService.asignarPermiso(id, request.permisoId());
        var response = adminMapper.toDetailResponse(rol);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/permisos/{permisoId}")
    @Operation(summary = "Remover permiso de rol", description = "Remueve un permiso de un rol")
    public ResponseEntity<RolDetailResponse> removerPermiso(
            @PathVariable Integer id,
            @PathVariable Integer permisoId) {
        log.info("Removiendo permiso {} del rol {}", permisoId, id);

        var rol = rolService.removerPermiso(id, permisoId);
        var response = adminMapper.toDetailResponse(rol);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de rol", description = "Activa o desactiva un rol")
    public ResponseEntity<RolResponse> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody ChangeRolStatusRequest request) {
        log.info("Cambiando estado de rol {} a: {}", id, request.activo());

        var rol = request.activo()
                ? rolService.activarRol(id)
                : rolService.desactivarRol(id);
        var response = adminMapper.toResponse(rol);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol", description = "Elimina un rol del sistema")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarRol(@PathVariable Integer id) {
        log.info("Eliminando rol con ID: {}", id);
        rolService.eliminarRol(id);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar roles", description = "Busca roles por nombre o descripción")
    public ResponseEntity<List<RolResponse>> buscarRoles(
            @Parameter(description = "Término de búsqueda") @RequestParam String q) {
        log.info("Buscando roles con término: {}", q);

        var roles = rolService.buscarRoles(q);
        var response = roles.stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/usuarios")
    @Operation(summary = "Usuarios con rol", description = "Lista todos los usuarios que tienen un rol específico")
    public ResponseEntity<List<AdminUserResponse>> usuariosConRol(@PathVariable Integer id) {
        log.info("Listando usuarios con rol ID: {}", id);

        var usuarios = rolService.obtenerUsuariosConRol(id);
        var response = usuarios.stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jerarquia")
    @Operation(summary = "Jerarquía de roles", description = "Obtiene la jerarquía completa de roles ordenada por prioridad")
    public ResponseEntity<List<RolHierarchyResponse>> obtenerJerarquia() {
        log.info("Obteniendo jerarquía de roles");

        var jerarquia = rolService.obtenerJerarquiaRoles();
        var response = jerarquia.stream()
                .map(adminMapper::toHierarchyResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sincronizar-sistema")
    @Operation(summary = "Sincronizar permisos del sistema", description = "Sincroniza automáticamente permisos basados en anotaciones de seguridad")
    public ResponseEntity<SyncPermissionsResponse> sincronizarPermisosSistema() {
        log.info("Sincronizando permisos del sistema");

        var resultado = rolService.sincronizarPermisosSistema();
        var response = adminMapper.toSyncResponse(resultado);

        return ResponseEntity.ok(response);
    }
}
