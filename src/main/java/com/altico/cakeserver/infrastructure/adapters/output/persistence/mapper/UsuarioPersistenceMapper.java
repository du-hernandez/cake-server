package com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper;

import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioPersistenceMapper {

    /**
     * Convierte una entidad de usuario a modelo de dominio
     */
    public UsuarioCompleto toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        // Convertir roles de String a RolCompleto básico
        Set<RolCompleto> roles = entity.getRoles().stream()
                .map(this::createBasicRolFromString)
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

        // Convertir roles de RolCompleto a String
        Set<String> rolesString = domain.getRoles().stream()
                .map(RolCompleto::getNombre)
                .collect(Collectors.toSet());
        entity.setRoles(rolesString);

        return entity;
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