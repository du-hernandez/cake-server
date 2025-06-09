package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Entidad de Refresh Token
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_username", columnList = "username"),
        @Index(name = "idx_refresh_token_device", columnList = "device_info"),
        @Index(name = "idx_refresh_token_expiracion", columnList = "fecha_expiracion"),
        @Index(name = "idx_refresh_token_activo", columnList = "activo")
})
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    private String id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "ultimo_uso")
    private LocalDateTime ultimoUso;

    public RefreshTokenEntity(String id, String username, String deviceInfo,
                              String ipAddress, String userAgent, LocalDateTime expiracion) {
        this.id = id;
        this.username = username;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaExpiracion = expiracion;
        this.ultimoUso = LocalDateTime.now();
    }

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public void marcarComoUsado() {
        this.ultimoUso = LocalDateTime.now();
    }

    public void revocar() {
        this.activo = false;
    }
}
