package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de dominio que representa una Ocasión
 * Inmutable para garantizar consistencia
 */
public class Ocasion {
    private final Integer id;
    private final String nombre;
    private final EstadoOcasion estado;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;

    // Constructor completo
    public Ocasion(Integer id, String nombre, EstadoOcasion estado,
                   LocalDateTime fechaCreado, LocalDateTime fechaActualizado) {
        validateNombre(nombre);

        this.id = id;
        this.nombre = nombre;
        this.estado = estado != null ? estado : EstadoOcasion.ACTIVO;
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
    }

    // Constructor para crear nueva ocasión
    private Ocasion(String nombre) {
        validateNombre(nombre);

        this.id = null;
        this.nombre = nombre;
        this.estado = EstadoOcasion.ACTIVO;
        this.fechaCreado = LocalDateTime.now();
        this.fechaActualizado = LocalDateTime.now();
    }

    // Factory method
    public static Ocasion crear(String nombre) {
        return new Ocasion(nombre);
    }

    // Métodos de negocio
    public Ocasion actualizarNombre(String nuevoNombre) {
        validateNombre(nuevoNombre);
        return new Ocasion(this.id, nuevoNombre, this.estado,
                this.fechaCreado, LocalDateTime.now());
    }

    public Ocasion activar() {
        if (this.estado == EstadoOcasion.ACTIVO) {
            return this;
        }
        return new Ocasion(this.id, this.nombre, EstadoOcasion.ACTIVO,
                this.fechaCreado, LocalDateTime.now());
    }

    public Ocasion desactivar() {
        if (this.estado == EstadoOcasion.INACTIVO) {
            return this;
        }
        return new Ocasion(this.id, this.nombre, EstadoOcasion.INACTIVO,
                this.fechaCreado, LocalDateTime.now());
    }

    // Validaciones
    private static void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ocasión no puede estar vacío");
        }
        if (nombre.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder 255 caracteres");
        }
    }

    // Queries
    public boolean estaActiva() {
        return estado == EstadoOcasion.ACTIVO;
    }

    // Getters
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public EstadoOcasion getEstado() { return estado; }
    public LocalDateTime getFechaCreado() { return fechaCreado; }
    public LocalDateTime getFechaActualizado() { return fechaActualizado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ocasion ocasion = (Ocasion) o;
        return Objects.equals(id, ocasion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ocasion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}