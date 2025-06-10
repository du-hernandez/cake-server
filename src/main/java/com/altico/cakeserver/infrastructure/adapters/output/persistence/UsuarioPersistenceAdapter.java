package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.UsuarioPersistencePort;
import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.domain.model.UsuarioEstadisticas;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.AdminPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioPersistenceAdapter implements UsuarioPersistencePort {

    private final UsuarioRepository usuarioRepository;
    private final AdminPersistenceMapper mapper;

    @Override
    public UsuarioCompleto save(UsuarioCompleto usuario) {
        log.debug("Guardando usuario: {}", usuario.getUsername());
        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity saved = usuarioRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioCompleto> findById(Long id) {
        return usuarioRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioCompleto> findByIdWithRoles(Long id) {
        // Para simplicidad, usamos la misma implementación
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioCompleto> findByUsername(String username) {
        return usuarioRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioCompleto> findByUsernameWithRoles(String username) {
        return findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioCompleto> findByEmail(String email) {
        return usuarioRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioCompleto> findAllWithFilters(Pageable pageable, Boolean activo, String rol) {
        return usuarioRepository.findAllWithFilters(activo, rol, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> searchByUsernameOrEmail(String termino) {
        return usuarioRepository.searchByUsernameOrEmail(termino).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> findByRole(String rol) {
        return usuarioRepository.findByRoles(rol).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> findWithoutRoles() {
        return usuarioRepository.findWithoutRoles().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> findInactiveUsersAfterDate(LocalDateTime fecha) {
        return usuarioRepository.findInactiveUsersAfterDate(fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> findUsersWithMultipleSessions() {
        return usuarioRepository.findUsersWithMultipleSessions().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRole(String rol) {
        return usuarioRepository.countByRole(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRoleAndActive(String rol, boolean activo) {
        return usuarioRepository.countByRoleAndActive(rol, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioEstadisticas getUsuarioEstadisticas() {
        Object[] stats = usuarioRepository.getUsuarioEstadisticas();

        log.info("ESTADISTICAS DE USUARIO: {}", stats);

//        // Extraer valores del array
//        long total = ((Number) stats[0]).longValue();
//        long activos = ((Number) stats[1]).longValue();
//        long inactivos = ((Number) stats[2]).longValue();
//        long sinRoles = ((Number) stats[3]).longValue();
//        long conMultiplesRoles = ((Number) stats[4]).longValue();

        long total = 0;
        long activos = 0;
        long inactivos = 0;
        long sinRoles = 0;
        long conMultiplesRoles = 0;

        // Validar y extraer el valor de 'total'
        if (stats.length > 0 && stats[0] instanceof Number) {
            total = ((Number) stats[0]).longValue();
        } else if (stats.length > 0 && stats[0] == null) {
            // Si es null, lo tratamos como 0 o manejamos la lógica específica
            total = 0; // O lanzas una excepción personalizada, o registras un warning
        }

        // Validar y extraer el valor de 'activos'
        if (stats.length > 1 && stats[1] instanceof Number) {
            activos = ((Number) stats[1]).longValue();
        } else if (stats.length > 1 && stats[1] == null) {
            activos = 0;
        }

        // Validar y extraer el valor de 'inactivos'
        if (stats.length > 2 && stats[2] instanceof Number) {
            inactivos = ((Number) stats[2]).longValue();
        } else if (stats.length > 2 && stats[2] == null) {
            inactivos = 0;
        }

        // Validar y extraer el valor de 'sinRoles'
        if (stats.length > 3 && stats[3] instanceof Number) {
            sinRoles = ((Number) stats[3]).longValue();
        } else if (stats.length > 3 && stats[3] == null) {
            sinRoles = 0;
        }

        // Validar y extraer el valor de 'conMultiplesRoles'
        if (stats.length > 4 && stats[4] instanceof Number) {
            conMultiplesRoles = ((Number) stats[4]).longValue();
        } else if (stats.length > 4 && stats[4] == null) {
            conMultiplesRoles = 0;
        }

        // Calcular estadísticas de tiempo
        LocalDateTime ahora = LocalDateTime.now();
        long ultimasHoras24 = usuarioRepository.countCreatedAfter(ahora.minusHours(24));
        long ultimosDias7 = usuarioRepository.countCreatedAfter(ahora.minusDays(7));
        long ultimosDias30 = usuarioRepository.countCreatedAfter(ahora.minusDays(30));

        return new UsuarioEstadisticas(
                total, activos, inactivos, sinRoles, conMultiplesRoles,
                ultimasHoras24, ultimosDias7, ultimosDias30
        );
    }
}
