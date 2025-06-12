package com.altico.cakeserver.domain.exception;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(Integer id) {
        super("Permiso con ID " + id + " no encontrado");
    }

    public PermissionNotFoundException(String message) {
        super(message);
    }
}
