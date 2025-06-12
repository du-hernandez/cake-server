package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// Entidad de Permiso - ACTUALIZADA
@Entity
@Table(name = "permisos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permiso_nombre", columnNames = "nombre"),
                @UniqueConstraint(name = "uk_permiso_recurso_accion", columnNames = {"recurso", "accion"})
        },
        indexes = {
                @Index(name = "idx_permiso_nombre", columnList = "nombre"),
                @Index(name = "idx_permiso_recurso", columnList = "recurso"),
                @Index(name = "idx_permiso_accion", columnList = "accion"),
                @Index(name = "idx_permiso_activo", columnList = "activo"),
                @Index(name = "idx_permiso_recurso_accion", columnList = "recurso, accion")
        })
@Getter
@Setter
@NoArgsConstructor
public class PermisoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "recurso", nullable = false, length = 50)
    private String recurso;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    // Relación many-to-many con roles
    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<RolEntity> roles = new HashSet<>();

    // Constructores
    public PermisoEntity(String nombre, String descripcion, String recurso, String accion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.recurso = recurso;
        this.accion = accion;
        this.activo = true;
    }

    public PermisoEntity(String nombre, String descripcion, String recurso, String accion, boolean activo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.recurso = recurso;
        this.accion = accion;
        this.activo = activo;
    }

    // Métodos de negocio
    public String getCodigoCompleto() {
        return recurso + ":" + accion;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public boolean estaEnUso() {
        return roles != null && !roles.isEmpty();
    }

    // Métodos de relación
    public void agregarRol(RolEntity rol) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(rol);
        if (rol.getPermisos() != null) {
            rol.getPermisos().add(this);
        }
    }

    public void removerRol(RolEntity rol) {
        if (roles != null) {
            roles.remove(rol);
        }
        if (rol != null && rol.getPermisos() != null) {
            rol.getPermisos().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermisoEntity that)) return false;
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PermisoEntity{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", recurso='" + recurso + '\'' +
                ", accion='" + accion + '\'' +
                ", activo=" + activo +
                '}';
    }
}