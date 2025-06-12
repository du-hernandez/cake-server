package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.applications.ports.input.RefreshTokenServicePort;
import com.altico.cakeserver.domain.model.RefreshToken;
import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.RolEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.UsuarioRepository;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.RolRepository;
import com.altico.cakeserver.infrastructure.config.JwtProperties;
import com.altico.cakeserver.infrastructure.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "API para autenticación y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository; // ✅ AGREGAR: Para buscar roles por nombre
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenServicePort refreshTokenService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        log.info("Intento de login para usuario: {}", request.username());

        try {
            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            // Obtener usuario
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UsuarioEntity usuario = usuarioRepository.findByUsername(request.username())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Generar access token
            String accessToken = jwtService.generateToken(userDetails);

            // Crear refresh token
            String deviceInfo = extractDeviceInfo(httpRequest);
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            var refreshToken = refreshTokenService.crearToken(
                    request.username(),
                    deviceInfo,
                    ipAddress,
                    userAgent
            );

            // Crear respuesta
            AuthResponse response = getAuthResponse(usuario, accessToken, refreshToken);

            log.info("Login exitoso para usuario: {}", request.username());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en login para usuario {}: {}", request.username(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        log.info("Registro de nuevo usuario: {}", request.username());

        try {
            // Verificar si ya existe
            if (usuarioRepository.existsByUsername(request.username())) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }

            if (usuarioRepository.existsByEmail(request.email())) {
                throw new RuntimeException("El email ya está registrado");
            }

            // Crear usuario
            UsuarioEntity usuario = new UsuarioEntity(
                    request.username(),
                    request.email(),
                    passwordEncoder.encode(request.password())
            );

            // ✅ CORRECCIÓN 1: Buscar y asignar rol por defecto usando RolEntity
            RolEntity rolUser = rolRepository.findByNombre("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
            usuario.getRoles().add(rolUser);

            // Guardar
            usuario = usuarioRepository.save(usuario);

            // Autenticar automáticamente
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generar access token
            String accessToken = jwtService.generateToken(userDetails);

            // Crear refresh token
            String deviceInfo = extractDeviceInfo(httpRequest);
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            var refreshToken = refreshTokenService.crearToken(
                    request.username(),
                    deviceInfo,
                    ipAddress,
                    userAgent
            );

            // Crear respuesta
            AuthResponse response = getAuthResponse(usuario, accessToken, refreshToken);

            log.info("Usuario registrado exitosamente: {}", request.username());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error en registro para usuario {}: {}", request.username(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Genera un nuevo token de acceso usando el token de refresco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token de refresco inválido")
    })
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        log.info("Solicitud de renovación de token");

        try {
            // Validar y renovar token
            if (!refreshTokenService.esTokenValido(request.refreshToken())) {
                log.warn("Token de refresco inválido: {}", request.refreshToken());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Obtener token actual
            var tokenActual = refreshTokenService.obtenerTokenPorId(request.refreshToken());

            // ✅ CORRECCIÓN 2: Cargar usuario y mapear roles correctamente
            UserDetails userDetails = usuarioRepository.findByUsername(tokenActual.username())
                    .map(usuario -> org.springframework.security.core.userdetails.User.builder()
                            .username(usuario.getUsername())
                            .password(usuario.getPassword())
                            .authorities(usuario.getRoles().stream()
                                    .map(rol -> new SimpleGrantedAuthority(rol.getNombre())) // ✅ CORREGIDO
                                    .collect(Collectors.toList()))
                            .build())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Generar nuevo access token
            String newAccessToken = jwtService.generateToken(userDetails);

            // Renovar refresh token
            var nuevoRefreshToken = refreshTokenService.renovarToken(request.refreshToken());

            // Obtener información del usuario
            UsuarioEntity usuario = usuarioRepository.findByUsername(tokenActual.username())
                    .orElseThrow();

            AuthResponse response = getAuthResponse(usuario, newAccessToken, nuevoRefreshToken);

            log.info("Token renovado exitosamente para usuario: {}", tokenActual.username());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al renovar token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ✅ CORRECCIÓN 3: Método helper para crear AuthResponse
    private AuthResponse getAuthResponse(UsuarioEntity usuario, String accessToken, RefreshToken refreshToken) {
        UserInfo userInfo = new UserInfo(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                // ✅ CORREGIDO: Convertir Set<RolEntity> a Set<String>
                usuario.getRoles().stream()
                        .map(RolEntity::getNombre)
                        .collect(Collectors.toSet()),
                usuario.isActivo()
        );

        return new AuthResponse(
                accessToken,
                refreshToken.id(),
                jwtProperties.getExpirationMs() / 1000,
                userInfo
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token y cierra la sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido")
    })
    public ResponseEntity<LogoutResponse> logout(
            @Valid @RequestBody LogoutRequest request,
            Authentication authentication) {

        log.info("Solicitud de logout para usuario: {}",
                authentication != null ? authentication.getName() : "desconocido");

        try {
            // Revocar refresh token
            if (authentication != null) {
                refreshTokenService.revocarTokenPorUsuario(request.refreshToken(), authentication.getName());
            } else {
                refreshTokenService.revocarToken(request.refreshToken());
            }

            var response = new LogoutResponse(
                    "Sesión cerrada exitosamente",
                    java.time.LocalDateTime.now()
            );

            log.info("Logout exitoso");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en logout: {}", e.getMessage());
            var response = new LogoutResponse(
                    "Error al cerrar sesión",
                    java.time.LocalDateTime.now()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Obtiene la información del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UserInfo> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ CORREGIDO: Convertir roles correctamente
        UserInfo userInfo = new UserInfo(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRoles().stream()
                        .map(RolEntity::getNombre)
                        .collect(Collectors.toSet()),
                usuario.isActivo()
        );

        return ResponseEntity.ok(userInfo);
    }

    // ============== MÉTODOS AUXILIARES ==============

    /**
     * Extrae información del dispositivo desde la petición HTTP
     */
    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown Device";

        // Lógica básica para extraer información del dispositivo
        if (userAgent.contains("Mobile")) {
            return "Mobile Device";
        } else if (userAgent.contains("Tablet")) {
            return "Tablet Device";
        } else if (userAgent.contains("Chrome")) {
            return "Desktop - Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Desktop - Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Desktop - Safari";
        } else {
            return "Desktop - Unknown Browser";
        }
    }

    /**
     * Obtiene la dirección IP real del cliente considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}