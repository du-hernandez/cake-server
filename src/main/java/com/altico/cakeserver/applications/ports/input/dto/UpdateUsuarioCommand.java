package com.altico.cakeserver.applications.ports.input.dto;

public record UpdateUsuarioCommand(
        String username,
        String email,
        String password
) {}
