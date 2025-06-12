package com.altico.cakeserver.infrastructure.adapters.input.rest.mapper;

import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.domain.model.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminDtoMapper {

    // ============== MAPPERS DE USUARIO ==============

    public CreateUsuarioCommand toCommand(CreateUserRequest request) {
        return new CreateUsuarioCommand(
                request.username(),
                request.email(),
                request.password(),
                request.activo(),
                request.roles()
        );
    }

    public UpdateUsuarioCommand toUpdateCommand(UpdateUserRequest request) {
        return new UpdateUsuarioCommand(
                request.username(),
                request.email(),
                request.password()
        );
    }

    public AdminUserResponse toResponse(UsuarioCompleto usuario) {
        return new AdminUserResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.isActivo(),
                usuario.getRoles().stream()
                        .map(RolCompleto::getNombre)
                        .collect(Collectors.toSet()),
                usuario.getFechaCreado(),
                usuario.getFechaActualizado(),
                usuario.getUltimoAcceso()
        );
    }

    public AdminUserDetailResponse toDetailResponse(UsuarioCompleto usuario) {
        return new AdminUserDetailResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.isActivo(),
                usuario.getRoles().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toSet()),
                usuario.getTodosLosPermisos(),
                usuario.getFechaCreado(),
                usuario.getFechaActualizado(),
                usuario.getUltimoAcceso(),
                createUserStatsDetail(usuario)
        );
    }

    public AdminUserListResponse toListResponse(Page<UsuarioCompleto> usuariosPage) {
        return new AdminUserListResponse(
                usuariosPage.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                new AdminUserListResponse.PageMetadata(
                        usuariosPage.getSize(),
                        usuariosPage.getTotalElements(),
                        usuariosPage.getTotalPages(),
                        usuariosPage.getNumber()
                )
        );
    }

    public UserStatsResponse toStatsResponse(UsuarioEstadisticas stats) {
        return new UserStatsResponse(
                stats.totalUsuarios(),
                stats.usuariosActivos(),
                stats.usuariosInactivos(),
                stats.usuariosSinRoles(),
                stats.usuariosConMultiplesRoles(),
                stats.ultimasHoras24(),
                stats.ultimosDias7(),
                stats.ultimosDias30()
        );
    }

    public UserAuditResponse toAuditResponse(UsuarioAuditoria auditoria) {
        return new UserAuditResponse(
                auditoria.id(),
                auditoria.accion(),
                auditoria.descripcion(),
                auditoria.ipAddress(),
                auditoria.userAgent(),
                auditoria.fecha(),
                auditoria.resultado()
        );
    }

    // ============== MAPPERS DE PERMISO ==============

    public CreatePermisoCommand toCommand(CreatePermisoRequest request) {
        return new CreatePermisoCommand(
                request.nombre(),
                request.descripcion(),
                request.recurso(),
                request.accion()
        );
    }

    public UpdatePermisoCommand toUpdateCommand(UpdatePermisoRequest request) {
        return new UpdatePermisoCommand(
                request.nombre(),
                request.descripcion()
        );
    }

    public PermisoResponse toResponse(Permiso permiso) {
        return new PermisoResponse(
                permiso.getId(),
                permiso.getNombre(),
                permiso.getDescripcion(),
                permiso.getRecurso(),
                permiso.getAccion(),
                permiso.getCodigoCompleto(),
                permiso.isActivo(),
                permiso.getFechaCreado(),
                permiso.getFechaActualizado()
        );
    }

    public PermisoListResponse toPermisoListResponse(Page<Permiso> permisosPage) {
        return new PermisoListResponse(
                permisosPage.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                new PermisoListResponse.PageMetadata(
                        permisosPage.getSize(),
                        permisosPage.getTotalElements(),
                        permisosPage.getTotalPages(),
                        permisosPage.getNumber()
                )
        );
    }

    // ============== MAPPERS DE ROL ==============

    public CreateRolCommand toCommand(CreateRolRequest request) {
        return new CreateRolCommand(
                request.nombre(),
                request.descripcion(),
                request.prioridad(),
                request.permisoIds()
        );
    }

    public UpdateRolCommand toUpdateCommand(UpdateRolRequest request) {
        return new UpdateRolCommand(
                request.nombre(),
                request.descripcion(),
                request.prioridad()
        );
    }

    public RolResponse toResponse(RolCompleto rol) {
        return new RolResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getPrioridad(),
                rol.isActivo(),
                rol.getPermisos().size(),
                0, // Se debe calcular desde el servicio si es necesario
                rol.getFechaCreado(),
                rol.getFechaActualizado()
        );
    }

    public RolDetailResponse toDetailResponse(RolCompleto rol) {
        return new RolDetailResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getPrioridad(),
                rol.isActivo(),
                rol.getPermisos().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toSet()),
                rol.getPermisosCodigosCompletos(),
                rol.getFechaCreado(),
                rol.getFechaActualizado()
        );
    }

    public RolListResponse toRolListResponse(Page<RolCompleto> rolesPage) {
        return new RolListResponse(
                rolesPage.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                new RolListResponse.PageMetadata(
                        rolesPage.getSize(),
                        rolesPage.getTotalElements(),
                        rolesPage.getTotalPages(),
                        rolesPage.getNumber()
                )
        );
    }

    public RolHierarchyResponse toHierarchyResponse(RolCompleto rol) {
        // El nivel se calcula basado en la prioridad
        int nivel = calcularNivelJerarquia(rol.getPrioridad());

        return new RolHierarchyResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getPrioridad(),
                nivel,
                rol.isActivo(),
                0, // Se debe calcular desde el servicio
                rol.getPermisosCodigosCompletos()
        );
    }

    public SyncPermissionsResponse toSyncResponse(SincronizacionResult resultado) {
        return new SyncPermissionsResponse(
                resultado.mensaje(),
                resultado.permisosCreados(),
                resultado.permisosActualizados(),
                resultado.permisosDesactivados(),
                resultado.nuevosRecursos(),
                resultado.nuevasAcciones(),
                resultado.fechaSincronizacion()
        );
    }

    // ============== MAPPERS DE REFRESH TOKEN ==============

    public RefreshTokenResponse toTokenResponse(RefreshToken token) {
        return new RefreshTokenResponse(
                token.id(),
                token.username(),
                token.deviceInfo(),
                token.ipAddress(),
                token.userAgent(),
                token.activo(),
                token.fechaCreacion(),
                token.fechaExpiracion(),
                token.ultimoUso()
        );
    }

    public RefreshTokenListResponse toTokenListResponse(Page<RefreshToken> tokensPage) {
        return new RefreshTokenListResponse(
                tokensPage.getContent().stream()
                        .map(this::toTokenResponse)
                        .collect(Collectors.toList()),
                new RefreshTokenListResponse.PageMetadata(
                        tokensPage.getSize(),
                        tokensPage.getTotalElements(),
                        tokensPage.getTotalPages(),
                        tokensPage.getNumber()
                )
        );
    }

    public TokenStatsResponse toTokenStatsResponse(TokenEstadisticas stats) {
        return new TokenStatsResponse(
                stats.totalTokens(),
                stats.tokensActivos(),
                stats.tokensExpirados(),
                stats.tokensRevocados(),
                stats.tokensPorExpirar24h(),
                stats.sesionesUnicas(),
                stats.dispositivosUnicos(),
                stats.promedioSesionesPorUsuario()
        );
    }

    public SuspiciousDeviceResponse toSuspiciousDeviceResponse(DispositivoSospechoso dispositivo) {
        return new SuspiciousDeviceResponse(
                dispositivo.deviceId(),
                dispositivo.deviceInfo(),
                dispositivo.ultimaIp(),
                dispositivo.usuariosDiferentes(),
                dispositivo.loginsFallidos(),
                dispositivo.razonSospecha(),
                dispositivo.primeraActividad(),
                dispositivo.ultimaActividad()
        );
    }

    // ============== MÉTODOS AUXILIARES ==============

    private UserStatsDetail createUserStatsDetail(UsuarioCompleto usuario) {
        // Crear estadísticas detalladas del usuario
        // Esto debería obtenerse de servicios especializados
        return new UserStatsDetail(
                0, // totalSesiones
                0, // sesionesActivas
                0, // totalLoginsFallidos
                usuario.getUltimoAcceso(), // ultimoLoginExitoso
                null, // ultimoLoginFallido
                "Unknown" // dispositivoMasUsado
        );
    }

    private int calcularNivelJerarquia(int prioridad) {
        // Convertir prioridad numérica a nivel jerárquico
        if (prioridad <= 10) return 1;  // Super Admin
        if (prioridad <= 50) return 2;  // Admin
        if (prioridad <= 100) return 3; // Manager
        if (prioridad <= 500) return 4; // User
        return 5; // Guest
    }
}
