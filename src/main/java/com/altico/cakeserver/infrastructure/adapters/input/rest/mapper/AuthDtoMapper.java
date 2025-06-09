package com.altico.cakeserver.infrastructure.adapters.input.rest.mapper;

import com.altico.cakeserver.domain.model.DispositivoSospechoso;
import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.domain.model.RolCompleto;
import com.altico.cakeserver.domain.model.UsuarioCompleto;
import com.altico.cakeserver.domain.model.TokenEstadisticas;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.RefreshTokenListResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.RefreshTokenResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.SuspiciousDeviceResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.TokenStatsResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthDtoMapper {

    public UserInfo toUserInfo(UsuarioCompleto usuario) {
        return new UserInfo(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRoles().stream()
                        .map(RolCompleto::getNombre)
                        .collect(Collectors.toSet()),
                usuario.isActivo()
        );
    }

    public AuthResponse toAuthResponse(String accessToken, String refreshToken,
                                       long expiresIn, UsuarioCompleto usuario) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                expiresIn,
                toUserInfo(usuario)
        );
    }

    // Métodos para mapear tokens (reutilizados del AdminDtoMapper)
    public RefreshTokenResponse toTokenResponse(RefreshToken token) {
        return new RefreshTokenResponse(
                token.id(),
                token.username(),
                token.deviceInfo(),
                token.ipAddress(),
                token.userAgent(),
                token.activo(),
                token.fechaCreacion(),
                token.fechaExpiracion(),
                token.ultimoUso()
        );
    }

    public RefreshTokenListResponse toTokenListResponse(org.springframework.data.domain.Page<RefreshToken> tokensPage) {
        return new RefreshTokenListResponse(
                tokensPage.getContent().stream()
                        .map(this::toTokenResponse)
                        .collect(Collectors.toList()),
                new RefreshTokenListResponse.PageMetadata(
                        tokensPage.getSize(),
                        tokensPage.getTotalElements(),
                        tokensPage.getTotalPages(),
                        tokensPage.getNumber()
                )
        );
    }

    public TokenStatsResponse toTokenStatsResponse(TokenEstadisticas stats) {
        return new TokenStatsResponse(
                stats.totalTokens(),
                stats.tokensActivos(),
                stats.tokensExpirados(),
                stats.tokensRevocados(),
                stats.tokensPorExpirar24h(),
                stats.sesionesUnicas(),
                stats.dispositivosUnicos(),
                stats.promedioSesionesPorUsuario()
        );
    }

    public SuspiciousDeviceResponse toSuspiciousDeviceResponse(DispositivoSospechoso dispositivo) {
        return new SuspiciousDeviceResponse(
                dispositivo.deviceId(),
                dispositivo.deviceInfo(),
                dispositivo.ultimaIp(),
                dispositivo.usuariosDiferentes(),
                dispositivo.loginsFallidos(),
                dispositivo.razonSospecha(),
                dispositivo.primeraActividad(),
                dispositivo.ultimaActividad()
        );
    }
}
