package com.altico.cakeserver.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class UsuarioCompleto {
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean activo;
    private final Set<RolCompleto> roles;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;
    private final LocalDateTime ultimoAcceso;

    public UsuarioCompleto(Long id, String username, String email, String password,
                           boolean activo, Set<RolCompleto> roles, LocalDateTime fechaCreado,
                           LocalDateTime fechaActualizado, LocalDateTime ultimoAcceso) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.activo = activo;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
        this.ultimoAcceso = ultimoAcceso;
    }

    public static UsuarioCompleto crear(String username, String email, String password) {
        return new UsuarioCompleto(null, username, email, password, true,
                new HashSet<>(), LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public UsuarioCompleto agregarRol(RolCompleto rol) {
        Set<RolCompleto> nuevosRoles = new HashSet<>(this.roles);
        nuevosRoles.add(rol);
        return new UsuarioCompleto(id, username, email, password, activo,
                nuevosRoles, fechaCreado, LocalDateTime.now(), ultimoAcceso);
    }

    public UsuarioCompleto removerRol(RolCompleto rol) {
        Set<RolCompleto> nuevosRoles = new HashSet<>(this.roles);
        nuevosRoles.remove(rol);
        return new UsuarioCompleto(id, username, email, password, activo,
                nuevosRoles, fechaCreado, LocalDateTime.now(), ultimoAcceso);
    }

    public UsuarioCompleto activar() {
        if (this.activo) return this;
        return new UsuarioCompleto(id, username, email, password, true,
                roles, fechaCreado, LocalDateTime.now(), ultimoAcceso);
    }

    public UsuarioCompleto desactivar() {
        if (!this.activo) return this;
        return new UsuarioCompleto(id, username, email, password, false,
                roles, fechaCreado, LocalDateTime.now(), ultimoAcceso);
    }

    public UsuarioCompleto actualizarUltimoAcceso() {
        return new UsuarioCompleto(id, username, email, password, activo,
                roles, fechaCreado, LocalDateTime.now(), LocalDateTime.now());
    }

    public boolean tienePermiso(String recurso, String accion) {
        return roles.stream()
                .filter(RolCompleto::isActivo)
                .anyMatch(rol -> rol.tienePermiso(recurso, accion));
    }

    public boolean tieneRol(String nombreRol) {
        return roles.stream()
                .filter(RolCompleto::isActivo)
                .anyMatch(rol -> rol.getNombre().equals(nombreRol));
    }

    public Set<String> getTodosLosPermisos() {
        return roles.stream()
                .filter(RolCompleto::isActivo)
                .flatMap(rol -> rol.getPermisosCodigosCompletos().stream())
                .collect(Collectors.toSet());
    }

    public RolCompleto getRolPrincipal() {
        return roles.stream()
                .filter(RolCompleto::isActivo)
                .min(Comparator.comparing(RolCompleto::getPrioridad))
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioCompleto that = (UsuarioCompleto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
