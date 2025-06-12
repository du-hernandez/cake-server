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
@Table(name = "ocasion", indexes = {
        @Index(name = "idx_ocasion_nombre", columnList = "nombre"),
        @Index(name = "idx_ocasion_estado", columnList = "estado")
})
@Getter
@Setter
@NoArgsConstructor
public class OcasionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "estado", nullable = false)
    private Byte estado;

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    // Relación con Tortas
    @OneToMany(mappedBy = "ocasion", cascade = CascadeType.ALL)
    private Set<TortaOcasionEntity> tortaOcasiones = new HashSet<>();

    // Constructor con parámetros esenciales
    public OcasionEntity(String nombre, Byte estado) {
        this.nombre = nombre;
        this.estado = estado;
    }

    // Método de utilidad para verificar si está activo
    public boolean isActivo() {
        return estado != null && estado == 1;
    }
}
