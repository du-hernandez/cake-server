package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "torta", indexes = {
        @Index(name = "idx_torta_descripcion", columnList = "descripcion"),
        @Index(name = "idx_torta_fecha_creado", columnList = "fecha_creado")
})
@Getter
@Setter
@NoArgsConstructor
public class TortaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "imagen", nullable = false, length = 500)
    private String imagen;

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    // Relación con Ocasiones
    @OneToMany(mappedBy = "torta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TortaOcasionEntity> tortaOcasiones = new HashSet<>();

    // Relación con Imágenes
    @OneToMany(mappedBy = "torta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fechaCreado DESC")
    private Set<ImagenEntity> imagenes = new HashSet<>();

    // Constructor con parámetros esenciales
    public TortaEntity(String descripcion, String imagen) {
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Métodos de utilidad para manejar relaciones
    public void addOcasion(OcasionEntity ocasion, Boolean estado) {
        TortaOcasionEntity tortaOcasion = new TortaOcasionEntity(this, ocasion, estado);
        tortaOcasiones.add(tortaOcasion);
    }

    public void removeOcasion(OcasionEntity ocasion) {
        tortaOcasiones.removeIf(to -> to.getOcasion().equals(ocasion));
    }

    public void addImagen(ImagenEntity imagen) {
        imagenes.add(imagen);
        imagen.setTorta(this);
    }

    public void removeImagen(ImagenEntity imagen) {
        imagenes.remove(imagen);
        imagen.setTorta(null);
    }
}