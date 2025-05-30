package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidad de dominio que representa una Imagen
 * Inmutable para garantizar consistencia
 */
public class Imagen {
    private final Integer id;
    private final String url;
    private final Integer tortaId;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;

    // Pattern para validación básica de URL
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$"
    );

    // Constructor completo
    public Imagen(Integer id, String url, Integer tortaId,
                  LocalDateTime fechaCreado, LocalDateTime fechaActualizado) {
        validateUrl(url);

        this.id = id;
        this.url = url;
        this.tortaId = tortaId;
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
    }

    // Constructor para crear nueva imagen
    private Imagen(String url, Integer tortaId) {
        validateUrl(url);

        this.id = null;
        this.url = url;
        this.tortaId = tortaId;
        this.fechaCreado = LocalDateTime.now();
        this.fechaActualizado = LocalDateTime.now();
    }

    // Factory method
    public static Imagen crear(String url, Integer tortaId) {
        return new Imagen(url, tortaId);
    }

    // Validaciones
    private static void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la imagen no puede estar vacía");
        }
        if (url.length() > 500) {
            throw new IllegalArgumentException("La URL de la imagen es demasiado larga");
        }
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new IllegalArgumentException("La URL de la imagen no es válida");
        }
    }

    // Queries
    public boolean esImagenExterna() {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public boolean perteneceATorta(Integer tortaId) {
        return this.tortaId != null && this.tortaId.equals(tortaId);
    }

    // Getters
    public Integer getId() { return id; }
    public String getUrl() { return url; }
    public Integer getTortaId() { return tortaId; }
    public LocalDateTime getFechaCreado() { return fechaCreado; }
    public LocalDateTime getFechaActualizado() { return fechaActualizado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Imagen imagen = (Imagen) o;
        return Objects.equals(id, imagen.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Imagen{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", tortaId=" + tortaId +
                '}';
    }
}
