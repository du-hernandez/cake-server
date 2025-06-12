package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Clase embebida para la clave compuesta
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class TortaOcasionId implements java.io.Serializable {

    @Column(name = "torta_id")
    private Integer tortaId;

    @Column(name = "ocasion_id")
    private Integer ocasionId;

    public TortaOcasionId(Integer tortaId, Integer ocasionId) {
        this.tortaId = tortaId;
        this.ocasionId = ocasionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TortaOcasionId that = (TortaOcasionId) o;
        return tortaId.equals(that.tortaId) && ocasionId.equals(that.ocasionId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(tortaId, ocasionId);
    }
}
