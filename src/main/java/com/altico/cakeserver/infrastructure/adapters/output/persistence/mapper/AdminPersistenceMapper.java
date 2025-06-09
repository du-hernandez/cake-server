package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminPersistenceMapper {

    // ============== MAPPERS DE USUARIO ==============

    public UsuarioCompleto toDomain(UsuarioEntity entity) {
        Set<RolCompleto> roles = entity.getRoles().stream()
                .map(this::rolStringToRolCompleto)
                .collect(Collectors.toSet());

        return new UsuarioCompleto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.isActivo(),
                roles,
                entity.getFechaCreado(),
                entity.getFechaActualizado(),
                null // ultimoAcceso se maneja por separado
        );
    }

    public UsuarioEntity toEntity(UsuarioCompleto domain) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setActivo(domain.isActivo());

        Set<String> rolesString = domain.getRoles().stream()
                .map(RolCompleto::getNombre)
                .collect(Collectors.toSet());
        entity.setRoles(rolesString);

        return entity;
    }

    // ============== MAPPERS DE PERMISO ==============

    public Permiso toDomain(PermisoEntity entity) {
        return new Permiso(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getRecurso(),
                entity.getAccion(),
                entity.isActivo(),
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    public PermisoEntity toEntity(Permiso domain) {
        PermisoEntity entity = new PermisoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setRecurso(domain.getRecurso());
        entity.setAccion(domain.getAccion());
        entity.setActivo(domain.isActivo());
        return entity;
    }

    // ============== MAPPERS DE ROL ==============

    public RolCompleto toDomain(RolEntity entity) {
        Set<Permiso> permisos = entity.getPermisos().stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());

        return new RolCompleto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrioridad(),
                entity.isActivo(),
                permisos,
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    public RolEntity toEntity(RolCompleto domain) {
        RolEntity entity = new RolEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setPrioridad(domain.getPrioridad());
        entity.setActivo(domain.isActivo());

        // Los permisos se manejan por separado en las relaciones
        return entity;
    }

    // ============== MAPPERS DE REFRESH TOKEN ==============

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUsername(),
                entity.getDeviceInfo(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.isActivo(),
                entity.getFechaCreacion(),
                entity.getFechaExpiracion(),
                entity.getUltimoUso()
        );
    }

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(domain.id());
        entity.setUsername(domain.username());
        entity.setDeviceInfo(domain.deviceInfo());
        entity.setIpAddress(domain.ipAddress());
        entity.setUserAgent(domain.userAgent());
        entity.setActivo(domain.activo());
        entity.setFechaCreacion(domain.fechaCreacion());
        entity.setFechaExpiracion(domain.fechaExpiracion());
        entity.setUltimoUso(domain.ultimoUso());
        return entity;
    }

    // ============== MAPPERS DE AUDITORÍA ==============

    public UsuarioAuditoria toDomain(AuditoriaEntity entity) {
        return new UsuarioAuditoria(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getAccion(),
                entity.getDescripcion(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getFecha(),
                entity.getResultado()
        );
    }

    public AuditoriaEntity toEntity(UsuarioAuditoria domain) {
        AuditoriaEntity entity = new AuditoriaEntity();
        entity.setId(domain.id());
        entity.setUsuarioId(domain.usuarioId());
        entity.setAccion(domain.accion());
        entity.setDescripcion(domain.descripcion());
        entity.setIpAddress(domain.ipAddress());
        entity.setUserAgent(domain.userAgent());
        entity.setFecha(domain.fecha());
        entity.setResultado(domain.resultado());
        return entity;
    }

    // ============== MÉTODOS AUXILIARES ==============

    private RolCompleto rolStringToRolCompleto(String rolString) {
        // Este método debería consultar la base de datos para obtener el rol completo
        // Por simplicidad, creamos un rol básico
        return new RolCompleto(
                null, // ID desconocido
                rolString,
                "Rol: " + rolString,
                999, // Prioridad por defecto
                true,
                Set.of(), // Sin permisos por ahora
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }
}
