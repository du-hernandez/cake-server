package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RolEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.RolRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioPersistenceMapper {

    private final RolRepository rolRepository;
    RolPersistenceMapper rolMapper;

    public UsuarioPersistenceMapper(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Convierte una entidad de usuario a modelo de dominio
     */
    public UsuarioCompleto toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        // ✅ CAMBIO: Convertir RolEntity a RolCompleto
        Set<RolCompleto> roles = entity.getRoles().stream()
//                .map(this::rolEntityToRolCompleto)
                .map(this::rolEntityToRolCompleto)
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

    /**
     * Convierte un modelo de dominio a entidad de usuario
     */
    public UsuarioEntity toEntity(UsuarioCompleto domain) {
        if (domain == null) return null;

        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setActivo(domain.isActivo());

        // ✅ CAMBIO: Convertir RolCompleto a RolEntity
        Set<RolEntity> rolesEntity = domain.getRoles().stream()
                .map(this::rolCompletoToRolEntity)
                .collect(Collectors.toSet());
        entity.setRoles(rolesEntity);

        return entity;
    }

    /**
     * Convierte RolEntity a RolCompleto
     */
    private RolCompleto rolEntityToRolCompleto(RolEntity entity) {
        return new RolCompleto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrioridad(),
                entity.isActivo(),
                new HashSet<>(), // Permisos se cargan por separado si es necesario
                entity.getFechaCreado(),
                entity.getFechaActualizado()
        );
    }

    /**
     * Convierte RolCompleto a RolEntity
     */
    private RolEntity rolCompletoToRolEntity(RolCompleto domain) {
        // Buscar la entidad existente por nombre
        return rolRepository.findByNombre(domain.getNombre())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rol no encontrado: " + domain.getNombre()));
    }

    /**
     * Crea un rol básico a partir de un string
     * Para casos donde solo necesitamos el nombre del rol
     */
    private RolCompleto createBasicRolFromString(String rolString) {
        return new RolCompleto(
                null, // ID desconocido
                rolString,
                "Rol: " + rolString,
                999, // Prioridad por defecto
                true,
                new HashSet<>(), // Sin permisos detallados
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }
}