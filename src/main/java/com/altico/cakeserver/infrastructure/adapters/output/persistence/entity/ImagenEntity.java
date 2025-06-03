package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagenes", indexes = {
        @Index(name = "idx_imagenes_fk_torta", columnList = "fk_torta"),
        @Index(name = "idx_imagenes_fecha_creado", columnList = "fecha_creado")
})
@Getter
@Setter
@NoArgsConstructor
public class ImagenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    // Relación con Torta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_torta", referencedColumnName = "id")
    private TortaEntity torta;

    // Constructor con parámetros esenciales
    public ImagenEntity(String url) {
        this.url = url;
    }

    // Constructor completo
    public ImagenEntity(String url, TortaEntity torta) {
        this.url = url;
        this.torta = torta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImagenEntity)) return false;
        ImagenEntity that = (ImagenEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
