package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Entidad de Auditoría
@Entity
@Table(name = "auditoria_usuarios", indexes = {
        @Index(name = "idx_auditoria_usuario", columnList = "usuario_id"),
        @Index(name = "idx_auditoria_fecha", columnList = "fecha"),
        @Index(name = "idx_auditoria_accion", columnList = "accion")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "resultado", length = 50)
    private String resultado;

    public AuditoriaEntity(Long usuarioId, String accion, String descripcion,
                           String ipAddress, String userAgent, String resultado) {
        this.usuarioId = usuarioId;
        this.accion = accion;
        this.descripcion = descripcion;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.fecha = LocalDateTime.now();
        this.resultado = resultado;
    }
}