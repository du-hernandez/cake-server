package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminPersistenceMapper {

    // ============== MAPPERS DE USUARIO ==============
    /**
     * Convierte una entidad UsuarioEntity a objeto de dominio UsuarioCompleto
     *
     * @param entity La entidad de usuario desde la base de datos
     * @return Objeto de dominio UsuarioCompleto
     */
    public UsuarioCompleto toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }

        // ✅ CORRECCIÓN: Mapear RolEntity a RolCompleto directamente
        Set<RolCompleto> roles = entity.getRoles().stream()
                .map(this::toDomain)  // Cambio de método
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

        // ✅ CORRECCIÓN: Mapear RolCompleto a RolEntity
        Set<RolEntity> rolesEntity = domain.getRoles().stream()
                .map(this::rolCompletoToRolEntity)
                .collect(Collectors.toSet());
        entity.setRoles(rolesEntity);

        return entity;
    }

    /**
     * Convierte una entidad RolEntity a objeto de dominio RolCompleto
     *
     * @param rolEntity La entidad de rol desde la base de datos
     * @return Objeto de dominio RolCompleto
     */
//    private RolCompleto rolEntityToRolCompleto(RolEntity rolEntity) {
//        if (rolEntity == null) {
//            return null;
//        }
//
//        Set<Permiso> permisos = rolEntity.getPermisos().stream()
//                .map(this::toDomain)
//                .collect(Collectors.toSet());
//
//        return new RolCompleto(
//                rolEntity.getId(),
//                rolEntity.getNombre(),
//                rolEntity.getDescripcion(),
//                rolEntity.getPrioridad(),
//                rolEntity.isActivo(),
//                permisos,
//                rolEntity.getFechaCreado(),
//                rolEntity.getFechaActualizado()
//                );
//    }

    /**
     * Convierte un objeto de dominio RolCompleto a entidad RolEntity
     *
     * @param rolCompleto El objeto de dominio de rol
     * @return Entidad RolEntity para persistencia
     */
    private RolEntity rolCompletoToRolEntity(RolCompleto rolCompleto) {
        if (rolCompleto == null) {
            return null;
        }

        RolEntity entity = new RolEntity();
        entity.setId(rolCompleto.getId());
        entity.setNombre(rolCompleto.getNombre());
        entity.setDescripcion(rolCompleto.getDescripcion());
        entity.setActivo(rolCompleto.isActivo());
        entity.setPrioridad(rolCompleto.getPrioridad());
        entity.setFechaCreado(rolCompleto.getFechaCreado());
        entity.setFechaActualizado(rolCompleto.getFechaActualizado());

        // Nota: Los permisos se manejan por separado para evitar problemas de cascada

        return entity;
    }

    /**
     * ✅ MÉTODO AUXILIAR: Mapea solo el nombre del rol (para compatibilidad con código legacy)
     * Este método puede ser útil si necesitas trabajar solo con nombres de roles
     *
     * @param rolEntity La entidad de rol
     * @return El nombre del rol como String
     */
    public String rolEntityToString(RolEntity rolEntity) {
        return rolEntity != null ? rolEntity.getNombre() : null;
    }

    /**
     * ✅ MÉTODO AUXILIAR: Crea una entidad de rol básica desde un nombre
     * Útil para casos donde solo tienes el nombre del rol
     *
     * @param rolNombre El nombre del rol
     * @return Una entidad RolEntity básica (solo con el nombre)
     */
    public RolEntity stringToRolEntity(String rolNombre) {
        if (rolNombre == null || rolNombre.trim().isEmpty()) {
            return null;
        }

        RolEntity entity = new RolEntity();
        entity.setNombre(rolNombre.trim());
        // Nota: En producción, deberías buscar el rol completo en la BD
        // Este método es solo para casos específicos
        return entity;
    }

    /**
     * ✅ MÉTODO AUXILIAR: Convierte un set de nombres de roles a set de RolEntity
     * Útil para migración de código legacy
     *
     * @param rolesNombres Set de nombres de roles
     * @return Set de RolEntity básicas
     */
    public Set<RolEntity> stringSetToRolEntitySet(Set<String> rolesNombres) {
        if (rolesNombres == null) {
            return new HashSet<>();
        }

        return rolesNombres.stream()
                .map(this::stringToRolEntity)
                .collect(Collectors.toSet());
    }

    /**
     * ✅ MÉTODO AUXILIAR: Convierte un set de RolEntity a set de nombres
     * Útil para compatibilidad con APIs que esperan solo nombres
     *
     * @param rolesEntity Set de entidades de rol
     * @return Set de nombres de roles
     */
    public Set<String> rolEntitySetToStringSet(Set<RolEntity> rolesEntity) {
        if (rolesEntity == null) {
            return new HashSet<>();
        }

        return rolesEntity.stream()
                .map(RolEntity::getNombre)
                .collect(Collectors.toSet());
    }

    /**
     * ✅ VALIDACIÓN: Verifica si un usuario tiene un rol específico
     *
     * @param usuario El usuario a verificar
     * @param rolNombre El nombre del rol a buscar
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean usuarioTieneRol(UsuarioCompleto usuario, String rolNombre) {
        if (usuario == null || usuario.getRoles() == null || rolNombre == null) {
            return false;
        }

        return usuario.getRoles().stream()
                .anyMatch(rol -> rolNombre.equals(rol.getNombre()));
    }

    /**
     * ✅ UTILIDAD: Obtiene los nombres de todos los roles de un usuario
     *
     * @param usuario El usuario del cual obtener los roles
     * @return Set con los nombres de los roles
     */
    public Set<String> obtenerNombresRoles(UsuarioCompleto usuario) {
        if (usuario == null || usuario.getRoles() == null) {
            return new HashSet<>();
        }

        return usuario.getRoles().stream()
                .map(RolCompleto::getNombre)
                .collect(Collectors.toSet());
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
