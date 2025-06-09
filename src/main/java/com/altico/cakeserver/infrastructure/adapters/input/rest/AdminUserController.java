package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.ports.input.UsuarioServicePort;
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
@RequestMapping("/api/v1/admin/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administración de Usuarios", description = "API para gestión administrativa de usuarios")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UsuarioServicePort usuarioService;
    private final AdminDtoMapper adminMapper;

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario con roles específicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuario ya existe"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<AdminUserResponse> crearUsuario(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Admin creando usuario: {}", request.username());

        var command = adminMapper.toCommand(request);
        var usuario = usuarioService.crearUsuario(command);
        var response = adminMapper.toResponse(usuario);

        var location = URI.create("/api/v1/admin/usuarios/" + usuario.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios con paginación")
    public ResponseEntity<AdminUserListResponse> listarUsuarios(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "username") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String direction,
            @Parameter(description = "Filtro por estado") @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Filtro por rol") @RequestParam(required = false) String rol) {

        log.info("Listando usuarios - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        var usuariosPage = usuarioService.listarUsuarios(pageable, activo, rol);
        var response = adminMapper.toListResponse(usuariosPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles completos de un usuario")
    public ResponseEntity<AdminUserDetailResponse> obtenerUsuario(@PathVariable Long id) {
        log.info("Obteniendo usuario con ID: {}", id);

        var usuario = usuarioService.obtenerPorId(id);
        var response = adminMapper.toDetailResponse(usuario);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario")
    public ResponseEntity<AdminUserResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Actualizando usuario con ID: {}", id);

        var command = adminMapper.toUpdateCommand(request);
        var usuario = usuarioService.actualizarUsuario(id, command);
        var response = adminMapper.toResponse(usuario);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de usuario", description = "Activa o desactiva un usuario")
    public ResponseEntity<AdminUserResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserStatusRequest request) {
        log.info("Cambiando estado de usuario {} a: {}", id, request.activo());

        var usuario = request.activo()
                ? usuarioService.activarUsuario(id)
                : usuarioService.desactivarUsuario(id);
        var response = adminMapper.toResponse(usuario);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Asignar rol", description = "Asigna un rol a un usuario")
    public ResponseEntity<AdminUserResponse> asignarRol(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request) {
        log.info("Asignando rol {} al usuario {}", request.rol(), id);

        var usuario = usuarioService.asignarRol(id, request.rol());
        var response = adminMapper.toResponse(usuario);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles/{rol}")
    @Operation(summary = "Remover rol", description = "Remueve un rol específico de un usuario")
    public ResponseEntity<AdminUserResponse> removerRol(
            @PathVariable Long id,
            @PathVariable String rol) {
        log.info("Removiendo rol {} del usuario {}", rol, id);

        var usuario = usuarioService.removerRol(id, rol);
        var response = adminMapper.toResponse(usuario);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente un usuario")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarUsuario(@PathVariable Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        usuarioService.eliminarUsuario(id);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre o email")
    public ResponseEntity<List<AdminUserResponse>> buscarUsuarios(
            @Parameter(description = "Término de búsqueda") @RequestParam String q) {
        log.info("Buscando usuarios con término: {}", q);

        var usuarios = usuarioService.buscarUsuarios(q);
        var response = usuarios.stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Estadísticas de usuarios", description = "Obtiene estadísticas generales de usuarios")
    public ResponseEntity<UserStatsResponse> obtenerEstadisticas() {
        log.info("Obteniendo estadísticas de usuarios");

        var stats = usuarioService.obtenerEstadisticas();
        var response = adminMapper.toStatsResponse(stats);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Resetear contraseña", description = "Genera una nueva contraseña temporal para el usuario")
    public ResponseEntity<ResetPasswordResponse> resetearPassword(@PathVariable Long id) {
        log.info("Reseteando contraseña para usuario: {}", id);

        var nuevaPassword = usuarioService.resetearPassword(id);
        var response = new ResetPasswordResponse(
                "Contraseña reseteada exitosamente",
                nuevaPassword,
                "El usuario debe cambiar esta contraseña en el próximo login"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auditoria/{id}")
    @Operation(summary = "Auditoría de usuario", description = "Obtiene el historial de acciones de un usuario")
    public ResponseEntity<List<UserAuditResponse>> obtenerAuditoria(@PathVariable Long id) {
        log.info("Obteniendo auditoría para usuario: {}", id);

        var auditoria = usuarioService.obtenerAuditoriaUsuario(id);
        var response = auditoria.stream()
                .map(adminMapper::toAuditResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
