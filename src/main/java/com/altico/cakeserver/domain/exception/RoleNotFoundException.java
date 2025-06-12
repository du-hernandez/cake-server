package com.altico.cakeserver.domain.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Integer id) {
        super("Rol con ID " + id + " no encontrado");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
