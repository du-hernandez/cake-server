package com.altico.cakeserver.domain.model;

public enum Rol {
    ADMIN("ROLE_ADMIN", "Administrador del sistema"),
    USER("ROLE_USER", "Usuario regular"),
    VIEWER("ROLE_VIEWER", "Solo lectura");

    private final String authority;
    private final String descripcion;

    Rol(String authority, String descripcion) {
        this.authority = authority;
        this.descripcion = descripcion;
    }

    public String getAuthority() { return authority; }
    public String getDescripcion() { return descripcion; }
}
