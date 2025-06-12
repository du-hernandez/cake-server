package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "torta_ocasion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"torta_id", "ocasion_id"})
        },
        indexes = {
                @Index(name = "idx_torta_ocasion_torta", columnList = "torta_id"),
                @Index(name = "idx_torta_ocasion_ocasion", columnList = "ocasion_id"),
                @Index(name = "idx_torta_ocasion_estado", columnList = "estado")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TortaOcasionEntity {

    @EmbeddedId
    private TortaOcasionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tortaId")
    @JoinColumn(name = "torta_id")
    private TortaEntity torta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ocasionId")
    @JoinColumn(name = "ocasion_id")
    private OcasionEntity ocasion;

    @Column(name = "estado", nullable = false)
    private Byte estado;

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    // Constructor
    public TortaOcasionEntity(TortaEntity torta, OcasionEntity ocasion, Boolean estado) {
        this.id = new TortaOcasionId(torta.getId(), ocasion.getId());
        this.torta = torta;
        this.ocasion = ocasion;
        this.estado = estado ? (byte) 1 : (byte) 0;
    }
}
