package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper específico para RefreshToken siguiendo principios de arquitectura hexagonal
 */
@Component
public class RefreshTokenPersistenceMapper {

    /**
     * Convierte una entidad de persistencia a modelo de dominio
     */
    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;

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

    /**
     * Convierte un modelo de dominio a entidad de persistencia
     */
    public RefreshTokenEntity toEntity(RefreshToken domain) {
        if (domain == null) return null;

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(domain.id());
        entity.setUsername(domain.username());
        entity.setDeviceInfo(domain.deviceInfo());
        entity.setIpAddress(domain.ipAddress());
        entity.setUserAgent(domain.userAgent());
        entity.setActivo(domain.activo());
        entity.setFechaExpiracion(domain.fechaExpiracion());

        // Solo establecer fechas si vienen del dominio (para actualizaciones)
        if (domain.fechaCreacion() != null) {
            entity.setFechaCreacion(domain.fechaCreacion());
        }
        if (domain.ultimoUso() != null) {
            entity.setUltimoUso(domain.ultimoUso());
        }

        return entity;
    }

    /**
     * Actualiza una entidad existente con datos del dominio
     * Útil para operaciones de actualización sin crear nueva entidad
     */
    public RefreshTokenEntity updateEntity(RefreshTokenEntity entity, RefreshToken domain) {
        if (entity == null || domain == null) return entity;

        entity.setUsername(domain.username());
        entity.setDeviceInfo(domain.deviceInfo());
        entity.setIpAddress(domain.ipAddress());
        entity.setUserAgent(domain.userAgent());
        entity.setActivo(domain.activo());
        entity.setFechaExpiracion(domain.fechaExpiracion());

        if (domain.ultimoUso() != null) {
            entity.setUltimoUso(domain.ultimoUso());
        }

        return entity;
    }

    /**
     * Convierte entidad a dominio con información mínima
     * Útil para listados donde no se necesita toda la información
     */
    public RefreshToken toDomainBasic(RefreshTokenEntity entity) {
        if (entity == null) return null;

        return new RefreshToken(
                entity.getId(),
                entity.getUsername(),
                entity.getDeviceInfo(),
                entity.getIpAddress(),
                null, // userAgent omitido para listados
                entity.isActivo(),
                entity.getFechaCreacion(),
                entity.getFechaExpiracion(),
                entity.getUltimoUso()
        );
    }
}