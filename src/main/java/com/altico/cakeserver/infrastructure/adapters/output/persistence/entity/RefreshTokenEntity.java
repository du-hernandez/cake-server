package com.altico.cakeserver.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA para RefreshToken con mejores prácticas de diseño
 */
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_username", columnList = "username"),
                @Index(name = "idx_refresh_token_device", columnList = "device_info"),
                @Index(name = "idx_refresh_token_expiracion", columnList = "fecha_expiracion"),
                @Index(name = "idx_refresh_token_activo", columnList = "activo"),
                @Index(name = "idx_refresh_token_ip", columnList = "ip_address")
        })
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    @Column(name = "id", length = 36)
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

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "ultimo_uso")
    @UpdateTimestamp
    private LocalDateTime ultimoUso;

    // Constructor completo
    public RefreshTokenEntity(String id, String username, String deviceInfo,
                              String ipAddress, String userAgent,
                              LocalDateTime fechaExpiracion) {
        this.id = id;
        this.username = username;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.activo = true;
        this.fechaExpiracion = fechaExpiracion;
        // fechaCreacion y ultimoUso se manejan automáticamente
    }

    // Métodos de negocio
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public boolean esValido() {
        return activo && !estaExpirado();
    }

    public void marcarComoUsado() {
        this.ultimoUso = LocalDateTime.now();
    }

    public void revocar() {
        this.activo = false;
    }

    public void renovarExpiracion(long minutosAdicionales) {
        this.fechaExpiracion = this.fechaExpiracion.plusMinutes(minutosAdicionales);
    }

    // Métodos de utilidad para auditoría
    public String getDeviceIdentifier() {
        return deviceInfo != null ? deviceInfo : "Unknown Device";
    }

    public boolean esDispositivoConocido(String otherDeviceInfo) {
        return deviceInfo != null && deviceInfo.equals(otherDeviceInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshTokenEntity that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RefreshTokenEntity{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", activo=" + activo +
                ", fechaExpiracion=" + fechaExpiracion +
                '}';
    }
}