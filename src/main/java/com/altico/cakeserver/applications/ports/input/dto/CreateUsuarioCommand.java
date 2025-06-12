package com.altico.cakeserver.applications.ports.input.dto;

import java.util.Set;

// Comandos para Usuario
public record CreateUsuarioCommand(
        String username,
        String email,
        String password,
        boolean activo,
        Set<String> roles
) {}
