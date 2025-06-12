package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.ports.input.RefreshTokenServicePort;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.RefreshTokenListResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.RefreshTokenResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.SuspiciousDeviceResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.admin.TokenStatsResponse;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth.*;
import com.altico.cakeserver.infrastructure.adapters.input.rest.mapper.AuthDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth/tokens")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Tokens", description = "API para gestión de refresh tokens y sesiones")
@SecurityRequirement(name = "bearerAuth")
public class RefreshTokenController {

    private final RefreshTokenServicePort refreshTokenService;
    private final AuthDtoMapper authMapper;

    @GetMapping("/mis-sesiones")
    @Operation(summary = "Mis sesiones activas", description = "Lista las sesiones activas del usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sesiones"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<RefreshTokenResponse>> misSesiones(Authentication authentication) {
        log.info("Usuario {} consultando sus sesiones", authentication.getName());

        var tokens = refreshTokenService.obtenerTokensActivosPorUsuario(authentication.getName());
        var response = tokens.stream()
                .map(authMapper::toTokenResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/mi-sesion/{tokenId}")
    @Operation(summary = "Cerrar mi sesión", description = "Revoca un token específico del usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sesión cerrada"),
            @ApiResponse(responseCode = "404", description = "Token no encontrado"),
            @ApiResponse(responseCode = "403", description = "Token no pertenece al usuario")
    })
    public ResponseEntity<Void> cerrarMiSesion(
            @PathVariable String tokenId,
            Authentication authentication) {
        log.info("Usuario {} cerrando sesión {}", authentication.getName(), tokenId);

        refreshTokenService.revocarTokenPorUsuario(tokenId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mis-sesiones")
    @Operation(summary = "Cerrar todas mis sesiones", description = "Revoca todos los tokens del usuario actual excepto el actual")
    public ResponseEntity<Void> cerrarTodasMisSesiones(Authentication authentication) {
        log.info("Usuario {} cerrando todas sus sesiones", authentication.getName());

        refreshTokenService.revocarTodosLosTokensDelUsuario(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verificar")
    @Operation(summary = "Verificar token", description = "Verifica si un refresh token es válido")
    public ResponseEntity<TokenValidationResponse> verificarToken(
            @Valid @RequestBody VerifyTokenRequest request) {
        log.info("Verificando validez de token");

        var esValido = refreshTokenService.esTokenValido(request.token());
        var response = new TokenValidationResponse(
                esValido,
                esValido ? "Token válido" : "Token inválido o expirado"
        );

        return ResponseEntity.ok(response);
    }

    // ============== ENDPOINTS ADMINISTRATIVOS ==============

    @GetMapping("/admin/sesiones")
    @Operation(summary = "Listar todas las sesiones", description = "Lista todas las sesiones activas del sistema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RefreshTokenListResponse> listarTodasLasSesiones(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaCreacion") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "DESC") String direction,
            @Parameter(description = "Filtro por usuario") @RequestParam(required = false) String username) {

        log.info("Admin listando sesiones - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        var tokensPage = refreshTokenService.listarTokens(pageable, username);
        var response = authMapper.toTokenListResponse(tokensPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/usuario/{username}")
    @Operation(summary = "Sesiones de usuario específico", description = "Lista todas las sesiones de un usuario específico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RefreshTokenResponse>> sesionesDeUsuario(@PathVariable String username) {
        log.info("Admin consultando sesiones del usuario: {}", username);

        var tokens = refreshTokenService.obtenerTokensActivosPorUsuario(username);
        var response = tokens.stream()
                .map(authMapper::toTokenResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/sesion/{tokenId}")
    @Operation(summary = "Revocar sesión específica", description = "Revoca un token específico por ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revocarSesion(@PathVariable String tokenId) {
        log.info("Admin revocando token: {}", tokenId);

        refreshTokenService.revocarToken(tokenId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/usuario/{username}/sesiones")
    @Operation(summary = "Revocar todas las sesiones de usuario", description = "Revoca todos los tokens de un usuario específico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenRevocationResponse> revocarSesionesDeUsuario(@PathVariable String username) {
        log.info("Admin revocando todas las sesiones del usuario: {}", username);

        int tokensRevocados = refreshTokenService.revocarTodosLosTokensDelUsuario(username);
        var response = new TokenRevocationResponse(
                "Sesiones revocadas exitosamente",
                tokensRevocados,
                username
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/expirados")
    @Operation(summary = "Limpiar tokens expirados", description = "Elimina todos los tokens expirados del sistema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenCleanupResponse> limpiarTokensExpirados() {
        log.info("Admin limpiando tokens expirados");

        int tokensEliminados = refreshTokenService.limpiarTokensExpirados();
        var response = new TokenCleanupResponse(
                "Limpieza completada",
                tokensEliminados,
                "Tokens expirados eliminados del sistema"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/estadisticas")
    @Operation(summary = "Estadísticas de tokens", description = "Obtiene estadísticas de tokens y sesiones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenStatsResponse> obtenerEstadisticas() {
        log.info("Admin obteniendo estadísticas de tokens");

        var stats = refreshTokenService.obtenerEstadisticas();
        var response = authMapper.toTokenStatsResponse(stats);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/invalidar-dispositivo")
    @Operation(summary = "Invalidar dispositivo", description = "Invalida todos los tokens de un dispositivo específico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenRevocationResponse> invalidarDispositivo(
            @Valid @RequestBody InvalidateDeviceRequest request) {
        log.info("Admin invalidando dispositivo: {}", request.deviceId());

        int tokensRevocados = refreshTokenService.invalidarDispositivo(request.deviceId());
        var response = new TokenRevocationResponse(
                "Dispositivo invalidado exitosamente",
                tokensRevocados,
                request.deviceId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/dispositivos-sospechosos")
    @Operation(summary = "Dispositivos sospechosos", description = "Lista dispositivos con actividad sospechosa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SuspiciousDeviceResponse>> dispositivosSospechosos() {
        log.info("Admin consultando dispositivos sospechosos");

        var dispositivos = refreshTokenService.obtenerDispositivosSospechosos();
        var response = dispositivos.stream()
                .map(authMapper::toSuspiciousDeviceResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
