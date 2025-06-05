package com.altico.cakeserver.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Getter
public class Usuario {
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean activo;
    private final Set<Rol> roles;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;

    public Usuario(Long id, String username, String email, String password,
                   boolean activo, Set<Rol> roles, LocalDateTime fechaCreado,
                   LocalDateTime fechaActualizado) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.activo = activo;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
    }

    public static Usuario crear(String username, String email, String password) {
        return new Usuario(null, username, email, password, true,
                new HashSet<>(), LocalDateTime.now(), LocalDateTime.now());
    }

    public Usuario agregarRol(Rol rol) {
        Set<Rol> nuevosRoles = new HashSet<>(this.roles);
        nuevosRoles.add(rol);
        return new Usuario(id, username, email, password, activo,
                nuevosRoles, fechaCreado, LocalDateTime.now());
    }
}
