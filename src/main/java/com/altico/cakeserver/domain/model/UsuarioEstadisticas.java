package com.altico.cakeserver.domain.model;

// Estadísticas de Usuario
public record UsuarioEstadisticas(
        long totalUsuarios,
        long usuariosActivos,
        long usuariosInactivos,
        long usuariosSinRoles,
        long usuariosConMultiplesRoles,
        long ultimasHoras24,
        long ultimosDias7,
        long ultimosDias30
) {}
