package com.altico.cakeserver.applications.service;

import com.altico.cakeserver.applications.ports.input.UsuarioServicePort;
import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.applications.ports.output.*;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioService implements UsuarioServicePort {

    private final UsuarioPersistencePort usuarioPersistence;
    private final RolPersistencePort rolPersistence;
    private final AuditoriaPersistencePort auditoriaPersistence;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public UsuarioCompleto crearUsuario(CreateUsuarioCommand command) {
        log.info("Creando usuario: {}", command.username());

        // Validaciones
        if (usuarioPersistence.existsByUsername(command.username())) {
            throw new DuplicateUserException("Username ya existe: " + command.username());
        }
        if (usuarioPersistence.existsByEmail(command.email())) {
            throw new DuplicateUserException("Email ya existe: " + command.email());
        }

        // Crear usuario
        UsuarioCompleto usuario = UsuarioCompleto.crear(
                command.username(),
                command.email(),
                passwordEncoder.encode(command.password())
        );

        if (!command.activo()) {
            usuario = usuario.desactivar();
        }

        // Guardar usuario base
        UsuarioCompleto usuarioGuardado = usuarioPersistence.save(usuario);

        // Asignar roles si se especificaron
        if (command.roles() != null && !command.roles().isEmpty()) {
            for (String nombreRol : command.roles()) {
                usuarioGuardado = asignarRolInterno(usuarioGuardado, nombreRol);
            }
        }

        // Auditoría
        registrarAuditoria(usuarioGuardado.getId(), "CREAR_USUARIO",
                "Usuario creado: " + command.username(), "EXITOSO");

        log.info("Usuario creado exitosamente: {}", command.username());
        return usuarioGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioCompleto obtenerPorId(Long id) {
        return usuarioPersistence.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioCompleto obtenerPorUsername(String username) {
        return usuarioPersistence.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public UsuarioCompleto actualizarUsuario(Long id, UpdateUsuarioCommand command) {
        log.info("Actualizando usuario con ID: {}", id);

        UsuarioCompleto usuario = obtenerPorId(id);

        // Validar cambios únicos
        if (command.username() != null && !command.username().equals(usuario.getUsername())) {
            if (usuarioPersistence.existsByUsername(command.username())) {
                throw new DuplicateUserException("Username ya existe: " + command.username());
            }
        }

        if (command.email() != null && !command.email().equals(usuario.getEmail())) {
            if (usuarioPersistence.existsByEmail(command.email())) {
                throw new DuplicateUserException("Email ya existe: " + command.email());
            }
        }

        // Crear usuario actualizado
        UsuarioCompleto usuarioActualizado = new UsuarioCompleto(
                usuario.getId(),
                command.username() != null ? command.username() : usuario.getUsername(),
                command.email() != null ? command.email() : usuario.getEmail(),
                command.password() != null ? passwordEncoder.encode(command.password()) : usuario.getPassword(),
                usuario.isActivo(),
                usuario.getRoles(),
                usuario.getFechaCreado(),
                LocalDateTime.now(),
                usuario.getUltimoAcceso()
        );

        UsuarioCompleto resultado = usuarioPersistence.save(usuarioActualizado);

        // Auditoría
        registrarAuditoria(id, "ACTUALIZAR_USUARIO",
                "Usuario actualizado: " + usuario.getUsername(), "EXITOSO");

        return resultado;
    }

    @Override
    public void eliminarUsuario(Long id) {
        log.info("Eliminando usuario con ID: {}", id);

        UsuarioCompleto usuario = obtenerPorId(id);

        // Validar si se puede eliminar
        if (usuario.tieneRol("ROLE_ADMIN") && contarAdministradores() <= 1) {
            throw new BusinessRuleViolationException("No se puede eliminar el último administrador");
        }

        usuarioPersistence.deleteById(id);

        // Auditoría
        registrarAuditoria(id, "ELIMINAR_USUARIO",
                "Usuario eliminado: " + usuario.getUsername(), "EXITOSO");

        log.info("Usuario eliminado: {}", usuario.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioCompleto> listarUsuarios(Pageable pageable, Boolean activo, String rol) {
        return usuarioPersistence.findAllWithFilters(pageable, activo, rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCompleto> buscarUsuarios(String termino) {
        return usuarioPersistence.searchByUsernameOrEmail(termino);
    }

    @Override
    public UsuarioCompleto activarUsuario(Long id) {
        log.info("Activando usuario con ID: {}", id);

        UsuarioCompleto usuario = obtenerPorId(id);
        if (usuario.isActivo()) {
            return usuario;
        }

        UsuarioCompleto usuarioActivo = usuario.activar();
        UsuarioCompleto resultado = usuarioPersistence.save(usuarioActivo);

        registrarAuditoria(id, "ACTIVAR_USUARIO", "Usuario activado", "EXITOSO");
        return resultado;
    }

    @Override
    public UsuarioCompleto desactivarUsuario(Long id) {
        log.info("Desactivando usuario con ID: {}", id);

        UsuarioCompleto usuario = obtenerPorId(id);

        // Validar si se puede desactivar
        if (usuario.tieneRol("ROLE_ADMIN") && contarAdministradoresActivos() <= 1) {
            throw new BusinessRuleViolationException("No se puede desactivar el último administrador activo");
        }

        if (!usuario.isActivo()) {
            return usuario;
        }

        UsuarioCompleto usuarioInactivo = usuario.desactivar();
        UsuarioCompleto resultado = usuarioPersistence.save(usuarioInactivo);

        registrarAuditoria(id, "DESACTIVAR_USUARIO", "Usuario desactivado", "EXITOSO");
        return resultado;
    }

    @Override
    public UsuarioCompleto asignarRol(Long usuarioId, String nombreRol) {
        log.info("Asignando rol {} al usuario {}", nombreRol, usuarioId);

        UsuarioCompleto usuario = obtenerPorId(usuarioId);
        return asignarRolInterno(usuario, nombreRol);
    }

    @Override
    public UsuarioCompleto removerRol(Long usuarioId, String nombreRol) {
        log.info("Removiendo rol {} del usuario {}", nombreRol, usuarioId);

        UsuarioCompleto usuario = obtenerPorId(usuarioId);

        // Validar si se puede remover
        if ("ROLE_ADMIN".equals(nombreRol) &&
                usuario.tieneRol("ROLE_ADMIN") &&
                contarAdministradores() <= 1) {
            throw new BusinessRuleViolationException("No se puede remover el rol ADMIN del último administrador");
        }

        RolCompleto rol = rolPersistence.findByNombre(nombreRol)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado: " + nombreRol));

        UsuarioCompleto usuarioActualizado = usuario.removerRol(rol);
        UsuarioCompleto resultado = usuarioPersistence.save(usuarioActualizado);

        registrarAuditoria(usuarioId, "REMOVER_ROL",
                "Rol removido: " + nombreRol, "EXITOSO");

        return resultado;
    }

    @Override
    public String resetearPassword(Long id) {
        log.info("Reseteando password para usuario: {}", id);

        UsuarioCompleto usuario = obtenerPorId(id);
        String nuevaPassword = generarPasswordTemporal();

        UsuarioCompleto usuarioActualizado = new UsuarioCompleto(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                passwordEncoder.encode(nuevaPassword),
                usuario.isActivo(),
                usuario.getRoles(),
                usuario.getFechaCreado(),
                LocalDateTime.now(),
                usuario.getUltimoAcceso()
        );

        usuarioPersistence.save(usuarioActualizado);

        registrarAuditoria(id, "RESETEAR_PASSWORD", "Password reseteado", "EXITOSO");

        return nuevaPassword;
    }

    @Override
    public UsuarioCompleto actualizarUltimoAcceso(String username) {
        UsuarioCompleto usuario = obtenerPorUsername(username);
        UsuarioCompleto usuarioActualizado = usuario.actualizarUltimoAcceso();
        return usuarioPersistence.save(usuarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAuditoria> obtenerAuditoriaUsuario(Long id) {
        return auditoriaPersistence.findByUsuarioId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioEstadisticas obtenerEstadisticas() {
        return usuarioPersistence.getUsuarioEstadisticas();
    }

    // Métodos auxiliares privados
    private UsuarioCompleto asignarRolInterno(UsuarioCompleto usuario, String nombreRol) {
        RolCompleto rol = rolPersistence.findByNombre(nombreRol)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado: " + nombreRol));

        if (usuario.tieneRol(nombreRol)) {
            return usuario; // Ya tiene el rol
        }

        UsuarioCompleto usuarioActualizado = usuario.agregarRol(rol);
        UsuarioCompleto resultado = usuarioPersistence.save(usuarioActualizado);

        registrarAuditoria(usuario.getId(), "ASIGNAR_ROL",
                "Rol asignado: " + nombreRol, "EXITOSO");

        return resultado;
    }

    private String generarPasswordTemporal() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return password.toString();
    }

    private long contarAdministradores() {
        return usuarioPersistence.countByRole("ROLE_ADMIN");
    }

    private long contarAdministradoresActivos() {
        return usuarioPersistence.countByRoleAndActive("ROLE_ADMIN", true);
    }

    private void registrarAuditoria(Long usuarioId, String accion, String descripcion, String resultado) {
        // Implementar auditoría
        auditoriaPersistence.save(new UsuarioAuditoria(
                null, usuarioId, accion, descripcion, null, null,
                LocalDateTime.now(), resultado
        ));
    }

    // Implementación de métodos restantes...
    @Override
    public List<UsuarioCompleto> obtenerUsuariosConRol(String rol) {
        return usuarioPersistence.findByRole(rol);
    }

    @Override
    public List<UsuarioCompleto> obtenerUsuariosSinRoles() {
        return usuarioPersistence.findWithoutRoles();
    }

    @Override
    public UsuarioCompleto sincronizarRoles(Long usuarioId, List<String> roles) {
        UsuarioCompleto usuario = obtenerPorId(usuarioId);

        // Remover todos los roles actuales
        UsuarioCompleto usuarioSinRoles = new UsuarioCompleto(
                usuario.getId(), usuario.getUsername(), usuario.getEmail(),
                usuario.getPassword(), usuario.isActivo(), Set.of(),
                usuario.getFechaCreado(), LocalDateTime.now(), usuario.getUltimoAcceso()
        );

        // Agregar nuevos roles
        UsuarioCompleto usuarioFinal = usuarioSinRoles;
        for (String nombreRol : roles) {
            usuarioFinal = asignarRolInterno(usuarioFinal, nombreRol);
        }

        return usuarioFinal;
    }

    @Override
    public UsuarioCompleto cambiarPassword(Long id, String nuevaPassword) {
        UsuarioCompleto usuario = obtenerPorId(id);
        UsuarioCompleto usuarioActualizado = new UsuarioCompleto(
                usuario.getId(), usuario.getUsername(), usuario.getEmail(),
                passwordEncoder.encode(nuevaPassword), usuario.isActivo(), usuario.getRoles(),
                usuario.getFechaCreado(), LocalDateTime.now(), usuario.getUltimoAcceso()
        );
        return usuarioPersistence.save(usuarioActualizado);
    }

    @Override
    public boolean validarPassword(Long id, String password) {
        UsuarioCompleto usuario = obtenerPorId(id);
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    @Override
    public void registrarIntentoCofcometoLoginFallido(String username, String ip) {
        // Implementar registro de intentos fallidos
        log.warn("Login fallido para usuario: {} desde IP: {}", username, ip);
    }

    @Override
    public boolean estaUsuarioBloqueado(String username) {
        // Implementar lógica de bloqueo por intentos fallidos
        return false;
    }

    @Override
    public List<UsuarioCompleto> obtenerUsuariosInactivosDesde(LocalDateTime fecha) {
        return usuarioPersistence.findInactiveUsersAfterDate(fecha);
    }

    @Override
    public List<UsuarioCompleto> obtenerUsuariosConMultiplesSesiones() {
        return usuarioPersistence.findUsersWithMultipleSessions();
    }

    @Override
    public boolean existeUsername(String username) {
        return usuarioPersistence.existsByUsername(username);
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioPersistence.existsByEmail(email);
    }

    @Override
    public boolean tienePermiso(Long usuarioId, String recurso, String accion) {
        UsuarioCompleto usuario = obtenerPorId(usuarioId);
        return usuario.tienePermiso(recurso, accion);
    }

    @Override
    public boolean tieneRol(Long usuarioId, String rol) {
        UsuarioCompleto usuario = obtenerPorId(usuarioId);
        return usuario.tieneRol(rol);
    }
}
