package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.UsuarioAuditoria;
import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.domain.model.UsuarioEstadisticas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Puerto de entrada para el servicio de gestión completa de usuarios
 */
public interface UsuarioServicePort {

    // Operaciones CRUD básicas
    UsuarioCompleto crearUsuario(CreateUsuarioCommand command);
    UsuarioCompleto obtenerPorId(Long id);
    UsuarioCompleto obtenerPorUsername(String username);
    UsuarioCompleto actualizarUsuario(Long id, UpdateUsuarioCommand command);
    void eliminarUsuario(Long id);

    // Búsquedas y listados
    Page<UsuarioCompleto> listarUsuarios(Pageable pageable, Boolean activo, String rol);
    List<UsuarioCompleto> buscarUsuarios(String termino);
    List<UsuarioCompleto> obtenerUsuariosConRol(String rol);
    List<UsuarioCompleto> obtenerUsuariosSinRoles();

    // Gestión de estado
    UsuarioCompleto activarUsuario(Long id);
    UsuarioCompleto desactivarUsuario(Long id);

    // Gestión de roles
    UsuarioCompleto asignarRol(Long usuarioId, String rol);
    UsuarioCompleto removerRol(Long usuarioId, String rol);
    UsuarioCompleto sincronizarRoles(Long usuarioId, List<String> roles);

    // Gestión de contraseñas
    String resetearPassword(Long id);
    UsuarioCompleto cambiarPassword(Long id, String nuevaPassword);
    boolean validarPassword(Long id, String password);

    // Operaciones de auditoría y seguridad
    UsuarioCompleto actualizarUltimoAcceso(String username);
    List<UsuarioAuditoria> obtenerAuditoriaUsuario(Long id);
    void registrarIntentoCofcometoLoginFallido(String username, String ip);
    boolean estaUsuarioBloqueado(String username);

    // Estadísticas y reportes
    UsuarioEstadisticas obtenerEstadisticas();
    List<UsuarioCompleto> obtenerUsuariosInactivosDesde(java.time.LocalDateTime fecha);
    List<UsuarioCompleto> obtenerUsuariosConMultiplesSesiones();

    // Validaciones
    boolean existeUsername(String username);
    boolean existeEmail(String email);
    boolean tienePermiso(Long usuarioId, String recurso, String accion);
    boolean tieneRol(Long usuarioId, String rol);
}
