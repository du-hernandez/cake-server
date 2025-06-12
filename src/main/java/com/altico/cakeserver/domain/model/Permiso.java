package com.altico.cakeserver.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Permiso {
    private final Integer id;
    private final String nombre;
    private final String descripcion;
    private final String recurso;
    private final String accion;
    private final boolean activo;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;

    public Permiso(Integer id, String nombre, String descripcion, String recurso,
                   String accion, boolean activo, LocalDateTime fechaCreado,
                   LocalDateTime fechaActualizado) {
        validateNombre(nombre);
        validateRecurso(recurso);
        validateAccion(accion);

        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.recurso = recurso;
        this.accion = accion;
        this.activo = activo;
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
    }

    public static Permiso crear(String nombre, String descripcion, String recurso, String accion) {
        return new Permiso(null, nombre, descripcion, recurso, accion, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    public Permiso activar() {
        if (this.activo) return this;
        return new Permiso(id, nombre, descripcion, recurso, accion, true,
                fechaCreado, LocalDateTime.now());
    }

    public Permiso desactivar() {
        if (!this.activo) return this;
        return new Permiso(id, nombre, descripcion, recurso, accion, false,
                fechaCreado, LocalDateTime.now());
    }

    public String getCodigoCompleto() {
        return recurso + ":" + accion;
    }

    private static void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del permiso no puede estar vacío");
        }
        if (nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
    }

    private static void validateRecurso(String recurso) {
        if (recurso == null || recurso.trim().isEmpty()) {
            throw new IllegalArgumentException("El recurso no puede estar vacío");
        }
    }

    private static void validateAccion(String accion) {
        if (accion == null || accion.trim().isEmpty()) {
            throw new IllegalArgumentException("La acción no puede estar vacía");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        return Objects.equals(id, permiso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
