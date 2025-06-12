package com.altico.cakeserver.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RolCompleto {
    private final Integer id;
    private final String nombre;
    private final String descripcion;
    private final int prioridad;
    private final boolean activo;
    private final Set<Permiso> permisos;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;

    public RolCompleto(Integer id, String nombre, String descripcion, int prioridad,
                       boolean activo, Set<Permiso> permisos, LocalDateTime fechaCreado,
                       LocalDateTime fechaActualizado) {
        validateNombre(nombre);
        validatePrioridad(prioridad);

        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.activo = activo;
        this.permisos = permisos != null ? new HashSet<>(permisos) : new HashSet<>();
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
    }

    public static RolCompleto crear(String nombre, String descripcion, int prioridad) {
        return new RolCompleto(null, nombre, descripcion, prioridad, true,
                new HashSet<>(), LocalDateTime.now(), LocalDateTime.now());
    }

    public RolCompleto agregarPermiso(Permiso permiso) {
        Set<Permiso> nuevosPermisos = new HashSet<>(this.permisos);
        nuevosPermisos.add(permiso);
        return new RolCompleto(id, nombre, descripcion, prioridad, activo,
                nuevosPermisos, fechaCreado, LocalDateTime.now());
    }

    public RolCompleto removerPermiso(Permiso permiso) {
        Set<Permiso> nuevosPermisos = new HashSet<>(this.permisos);
        nuevosPermisos.remove(permiso);
        return new RolCompleto(id, nombre, descripcion, prioridad, activo,
                nuevosPermisos, fechaCreado, LocalDateTime.now());
    }

    public RolCompleto activar() {
        if (this.activo) return this;
        return new RolCompleto(id, nombre, descripcion, prioridad, true,
                permisos, fechaCreado, LocalDateTime.now());
    }

    public RolCompleto desactivar() {
        if (!this.activo) return this;
        return new RolCompleto(id, nombre, descripcion, prioridad, false,
                permisos, fechaCreado, LocalDateTime.now());
    }

    public boolean tienePermiso(String recurso, String accion) {
        return permisos.stream()
                .filter(Permiso::isActivo)
                .anyMatch(p -> p.getRecurso().equals(recurso) && p.getAccion().equals(accion));
    }

    public boolean tienePermiso(String codigoCompleto) {
        return permisos.stream()
                .filter(Permiso::isActivo)
                .anyMatch(p -> p.getCodigoCompleto().equals(codigoCompleto));
    }

    public Set<String> getPermisosCodigosCompletos() {
        return permisos.stream()
                .filter(Permiso::isActivo)
                .map(Permiso::getCodigoCompleto)
                .collect(Collectors.toSet());
    }

    private static void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder 50 caracteres");
        }
    }

    private static void validatePrioridad(int prioridad) {
        if (prioridad < 0 || prioridad > 1000) {
            throw new IllegalArgumentException("La prioridad debe estar entre 0 y 1000");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolCompleto that = (RolCompleto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
