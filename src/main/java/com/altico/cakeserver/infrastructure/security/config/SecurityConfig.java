package com.altico.cakeserver.infrastructure.security.config;

import com.altico.cakeserver.infrastructure.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ============== ENDPOINTS PÚBLICOS ==============
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // ============== ENDPOINTS DE SOLO LECTURA ==============
                        // Permitir a todos los autenticados
                        .requestMatchers(HttpMethod.GET, "/api/v1/tortas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/ocasiones/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/imagenes/**").authenticated()

                        // ============== ENDPOINTS DE GESTIÓN BÁSICA ==============
                        // Solo ADMIN y USER pueden crear/modificar
                        .requestMatchers(HttpMethod.POST, "/api/v1/tortas/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tortas/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tortas/**").hasAnyRole("ADMIN", "USER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/ocasiones/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/ocasiones/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/ocasiones/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/imagenes/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/imagenes/**").hasAnyRole("ADMIN", "USER")

                        // ============== GESTIÓN DE TOKENS ==============
                        // Usuarios pueden gestionar sus propias sesiones
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/tokens/mis-sesiones").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/tokens/mi-sesion/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/tokens/mis-sesiones").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/tokens/verificar").authenticated()

                        // Administradores pueden gestionar todas las sesiones
                        .requestMatchers("/api/v1/auth/tokens/admin/**").hasRole("ADMIN")

                        // ============== ADMINISTRACIÓN AVANZADA ==============
                        // Solo administradores
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // ============== CUALQUIER OTRA PETICIÓN ==============
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Versión deprecada
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // La recomendación es pasar UserDetailsService en el constructor y luego setear el PasswordEncoder.
        // Aunque la recomendación es de setPasswordEncoder(), la forma más idiomática con el constructor
        // es pasar ambos si el constructor lo permite o usar el patrón builder si se ofrece.
        // Sin embargo, para DaoAuthenticationProvider, el constructor más adecuado para evitar la advertencia
        // es el que recibe UserDetailsService y luego se llama a setPasswordEncoder.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}