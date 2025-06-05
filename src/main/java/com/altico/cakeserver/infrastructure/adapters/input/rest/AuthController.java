package com.altico.cakeserver.infrastructure.adapters.input.rest;

import com.altico.cakeserver.infrastructure.adapters.input.rest.dto.auth.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.UsuarioRepository;
import com.altico.cakeserver.infrastructure.config.JwtProperties;
import com.altico.cakeserver.infrastructure.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.username());

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
                .orElseThrow();

        // Generar tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Crear respuesta
        UserInfo userInfo = new UserInfo(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.isActivo()
        );

        AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken,
                86400, // 24 horas en segundos
                userInfo
        );

        log.info("Login exitoso para usuario: {}", request.username());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registro de nuevo usuario: {}", request.username());

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

        // Asignar rol por defecto
        usuario.getRoles().add("ROLE_USER");

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

        // Generar tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Crear respuesta
        UserInfo userInfo = new UserInfo(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.isActivo()
        );

        AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken,
                jwtProperties.getExpirationMs(),
                userInfo
        );

        log.info("Usuario registrado exitosamente: {}", request.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Genera un nuevo token de acceso usando el token de refresco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token de refresco inválido")
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Solicitud de renovación de token");

        try {
            // Extraer username del refresh token
            String username = jwtService.extractUsername(request.refreshToken());

            // Cargar usuario
            UserDetails userDetails = usuarioRepository.findByUsername(username)
                    .map(usuario -> org.springframework.security.core.userdetails.User.builder()
                            .username(usuario.getUsername())
                            .password(usuario.getPassword())
                            .authorities(usuario.getRoles().stream()
                                    .map(rol -> new org.springframework.security.core.authority.SimpleGrantedAuthority(rol))
                                    .collect(Collectors.toList()))
                            .build())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar refresh token
            if (!jwtService.validateToken(request.refreshToken(), userDetails)) {
                throw new RuntimeException("Token de refresco inválido");
            }

            // Generar nuevo access token
            String newAccessToken = jwtService.generateToken(userDetails);

            // Obtener información del usuario
            UsuarioEntity usuario = usuarioRepository.findByUsername(username).orElseThrow();

            UserInfo userInfo = new UserInfo(
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getRoles(),
                    usuario.isActivo()
            );

            AuthResponse response = new AuthResponse(
                    newAccessToken,
                    request.refreshToken(), // Mantener el mismo refresh token
                    86400,
                    userInfo
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al renovar token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

        UserInfo userInfo = new UserInfo(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.isActivo()
        );

        return ResponseEntity.ok(userInfo);
    }
}