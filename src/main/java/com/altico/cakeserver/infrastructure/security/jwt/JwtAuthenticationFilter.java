package com.altico.cakeserver.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Si no hay header o no empieza con Bearer, continuar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer token
        jwt = authHeader.substring(7);

        try {
            // Extraer username del token
            username = jwtService.extractUsername(jwt);

            // Si el username es válido y no hay autenticación en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargar usuario
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Validar token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // Crear autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Usuario autenticado: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("No se pudo autenticar usuario: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}