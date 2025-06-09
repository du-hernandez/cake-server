package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.DispositivoSospechoso;
import com.altico.cakeserver.domain.model.IntentoTokenSospechoso;
import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.domain.model.TokenEstadisticas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada para el servicio de gestión de refresh tokens
 */
public interface RefreshTokenServicePort {

    // Operaciones básicas de tokens
    RefreshToken crearToken(String username, String deviceInfo, String ipAddress, String userAgent);
    RefreshToken renovarToken(String tokenId);
    void revocarToken(String tokenId);
    boolean esTokenValido(String tokenId);

    // Gestión por usuario
    List<RefreshToken> obtenerTokensActivosPorUsuario(String username);
    int revocarTodosLosTokensDelUsuario(String username);
    void revocarTokenPorUsuario(String tokenId, String username);

    // Operaciones administrativas
    Page<RefreshToken> listarTokens(Pageable pageable, String username);
    int limpiarTokensExpirados();
    int invalidarDispositivo(String deviceId);
    List<DispositivoSospechoso> obtenerDispositivosSospechosos();

    // Consultas y validaciones
    RefreshToken obtenerTokenPorId(String tokenId);
    boolean existeTokenParaUsuario(String username, String deviceId);
    int contarTokensActivosPorUsuario(String username);
    List<String> obtenerDispositivosDelUsuario(String username);

    // Seguridad y auditoría
    void registrarUsoToken(String tokenId);
    void registrarIntentoCofcometoTokenInvalido(String tokenId, String ip);
    List<IntentoTokenSospechoso> obtenerIntentosTokensSospechosos();

    // Estadísticas
    TokenEstadisticas obtenerEstadisticas();
    List<RefreshToken> obtenerTokensPorExpirar(int horas);
    Map<String, Integer> obtenerEstadisticasPorDispositivo();

    // Configuración y mantenimiento
    void configurarTiempoExpiracion(long minutos);
    void configurarLimiteTokensPorUsuario(int limite);
    void configurarLimpiezaAutomatica(boolean habilitada);
}
