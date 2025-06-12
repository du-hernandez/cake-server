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
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean activo = true;

    // Relación ManyToMany con RolEntity
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<RolEntity> roles = new HashSet<>();

    @Column(name = "fecha_creado", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreado;

    @Column(name = "fecha_actualizado")
    @UpdateTimestamp
    private LocalDateTime fechaActualizado;

    public UsuarioEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.activo = true;
    }

    // Métodos de conveniencia
    public void agregarRol(RolEntity rol) {
        this.roles.add(rol);
    }

    public void removerRol(RolEntity rol) {
        this.roles.remove(rol);
    }

    public boolean tieneRol(String nombreRol) {
        return roles.stream()
                .anyMatch(rol -> rol.getNombre().equals(nombreRol));
    }

    public Set<String> getNombresRoles() {
        return roles.stream()
                .map(RolEntity::getNombre)
                .collect(Collectors.toSet());
    }
}