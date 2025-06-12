package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.domain.model.UsuarioEstadisticas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Usuarios completos
 */
public interface UsuarioPersistencePort {

    // Operaciones CRUD básicas
    UsuarioCompleto save(UsuarioCompleto usuario);
    Optional<UsuarioCompleto> findById(Long id);
    Optional<UsuarioCompleto> findByIdWithRoles(Long id);
    Optional<UsuarioCompleto> findByUsername(String username);
    Optional<UsuarioCompleto> findByUsernameWithRoles(String username);
    Optional<UsuarioCompleto> findByEmail(String email);
    void deleteById(Long id);
    boolean existsById(Long id);

    // Validaciones de duplicados
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Búsquedas y listados
    Page<UsuarioCompleto> findAllWithFilters(Pageable pageable, Boolean activo, String rol);
    List<UsuarioCompleto> searchByUsernameOrEmail(String termino);
    List<UsuarioCompleto> findByRole(String rol);
    List<UsuarioCompleto> findWithoutRoles();

    // Consultas por estado y actividad
    List<UsuarioCompleto> findInactiveUsersAfterDate(LocalDateTime fecha);
    List<UsuarioCompleto> findUsersWithMultipleSessions();
    long countByRole(String rol);
    long countByRoleAndActive(String rol, boolean activo);

    // Estadísticas
    UsuarioEstadisticas getUsuarioEstadisticas();
}