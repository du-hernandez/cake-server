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

// Entidad de Rol Completo - ACTUALIZADA
@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_rol_nombre", columnList = "nombre"),
        @Index(name = "idx_rol_prioridad", columnList = "prioridad"),
        @Index(name = "idx_rol_activo", columnList = "activo")
})
@Getter
@Setter
@NoArgsConstructor
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "prioridad", nullable = false)
    private int prioridad;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    // Relación many-to-many con permisos
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_permisos",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<PermisoEntity> permisos = new HashSet<>();

    public RolEntity(String nombre, String descripcion, int prioridad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.activo = true;
    }

    public void agregarPermiso(PermisoEntity permiso) {
        if (permisos == null) {
            permisos = new HashSet<>();
        }
        permisos.add(permiso);
        permiso.getRoles().add(this);
    }

    public void removerPermiso(PermisoEntity permiso) {
        if (permisos != null) {
            permisos.remove(permiso);
        }
        if (permiso != null && permiso.getRoles() != null) {
            permiso.getRoles().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolEntity that)) return false;
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RolEntity{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", prioridad=" + prioridad +
                ", activo=" + activo +
                '}';
    }
}