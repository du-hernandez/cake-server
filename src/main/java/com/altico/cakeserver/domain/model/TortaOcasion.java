package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object que representa la relación entre Torta y Ocasión
 */
public class TortaOcasion {
    private final Integer tortaId;
    private final Integer ocasionId;
    private final boolean activo;
    private final LocalDateTime fechaCreado;
    private final LocalDateTime fechaActualizado;

    public TortaOcasion(Integer tortaId, Integer ocasionId, boolean activo,
                        LocalDateTime fechaCreado, LocalDateTime fechaActualizado) {
        validateIds(tortaId, ocasionId);

        this.tortaId = tortaId;
        this.ocasionId = ocasionId;
        this.activo = activo;
        this.fechaCreado = fechaCreado;
        this.fechaActualizado = fechaActualizado;
    }

    // Constructor para crear nueva relación
    public static TortaOcasion crear(Integer tortaId, Integer ocasionId) {
        return new TortaOcasion(tortaId, ocasionId, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    // Métodos de negocio
    public TortaOcasion activar() {
        if (this.activo) {
            return this;
        }
        return new TortaOcasion(this.tortaId, this.ocasionId, true,
                this.fechaCreado, LocalDateTime.now());
    }

    public TortaOcasion desactivar() {
        if (!this.activo) {
            return this;
        }
        return new TortaOcasion(this.tortaId, this.ocasionId, false,
                this.fechaCreado, LocalDateTime.now());
    }

    // Validaciones
    private static void validateIds(Integer tortaId, Integer ocasionId) {
        if (tortaId == null || tortaId <= 0) {
            throw new IllegalArgumentException("ID de torta inválido");
        }
        if (ocasionId == null || ocasionId <= 0) {
            throw new IllegalArgumentException("ID de ocasión inválido");
        }
    }

    // Getters
    public Integer getTortaId() { return tortaId; }
    public Integer getOcasionId() { return ocasionId; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreado() { return fechaCreado; }
    public LocalDateTime getFechaActualizado() { return fechaActualizado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TortaOcasion that = (TortaOcasion) o;
        return Objects.equals(tortaId, that.tortaId) &&
                Objects.equals(ocasionId, that.ocasionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tortaId, ocasionId);
    }
}