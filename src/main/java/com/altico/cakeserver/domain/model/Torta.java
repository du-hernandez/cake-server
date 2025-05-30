package com.altico.cakeserver.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Entidad de dominio que representa una Torta
 * Inmutable para garantizar consistencia
 */
@Getter
public class Torta {
    private final Integer id;
    private final String descripcion;
    private final String imagen;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;
    private final Set<Ocasion> ocasiones;
    private final Set<Imagen> imagenes;

    // Constructor completo (para reconstruir desde persistencia)
    public Torta(Integer id, String descripcion, String imagen,
                 LocalDateTime fechaCreado, LocalDateTime fechaActualizado,
                 Set<Ocasion> ocasiones, Set<Imagen> imagenes) {
        validateDescripcion(descripcion);

        this.id = id;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
        this.ocasiones = ocasiones != null ? new HashSet<>(ocasiones) : new HashSet<>();
        this.imagenes = imagenes != null ? new HashSet<>(imagenes) : new HashSet<>();
    }

    // Constructor para crear nueva torta
    private Torta(String descripcion, String imagen) {
        validateDescripcion(descripcion);

        this.id = null;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.fechaCreado = LocalDateTime.now();
        this.fechaActualizado = LocalDateTime.now();
        this.ocasiones = new HashSet<>();
        this.imagenes = new HashSet<>();
    }

    // Factory method para crear nueva torta
    public static Torta crear(String descripcion, String imagen) {
        return new Torta(descripcion, imagen);
    }

    // Métodos de negocio
    public Torta actualizarDescripcion(String nuevaDescripcion) {
        return new Torta(this.id, nuevaDescripcion, this.imagen,
                this.fechaCreado, LocalDateTime.now(),
                this.ocasiones, this.imagenes);
    }

    public Torta actualizarImagen(String nuevaImagen) {
        return new Torta(this.id, this.descripcion, nuevaImagen,
                this.fechaCreado, LocalDateTime.now(),
                this.ocasiones, this.imagenes);
    }

    public Torta agregarOcasion(Ocasion ocasion) {
        Set<Ocasion> nuevasOcasiones = new HashSet<>(this.ocasiones);
        nuevasOcasiones.add(ocasion);
        return new Torta(this.id, this.descripcion, this.imagen,
                this.fechaCreado, LocalDateTime.now(),
                nuevasOcasiones, this.imagenes);
    }

    public Torta removerOcasion(Ocasion ocasion) {
        Set<Ocasion> nuevasOcasiones = new HashSet<>(this.ocasiones);
        nuevasOcasiones.remove(ocasion);
        return new Torta(this.id, this.descripcion, this.imagen,
                this.fechaCreado, LocalDateTime.now(),
                nuevasOcasiones, this.imagenes);
    }

    public Torta agregarImagen(Imagen imagen) {
        Set<Imagen> nuevasImagenes = new HashSet<>(this.imagenes);
        nuevasImagenes.add(imagen);
        return new Torta(this.id, this.descripcion, this.imagen,
                this.fechaCreado, LocalDateTime.now(),
                this.ocasiones, nuevasImagenes);
    }

    // Validaciones
    private static void validateDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        if (descripcion.length() > 255) {
            throw new IllegalArgumentException("La descripción no puede exceder 255 caracteres");
        }
    }

    // Queries
    public boolean tieneOcasion(Integer ocasionId) {
        return ocasiones.stream()
                .anyMatch(o -> o.getId().equals(ocasionId));
    }

    public int cantidadImagenes() {
        return imagenes.size();
    }

    public boolean tieneImagenPrincipal() {
        return imagen != null && !imagen.trim().isEmpty();
    }

    // Getters (solo lectura)
    public Set<Ocasion> getOcasiones() { return Collections.unmodifiableSet(ocasiones); }
    public Set<Imagen> getImagenes() { return Collections.unmodifiableSet(imagenes); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Torta torta = (Torta) o;
        return Objects.equals(id, torta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Torta{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", cantidadOcasiones=" + ocasiones.size() +
                ", cantidadImagenes=" + imagenes.size() +
                '}';
    }
}