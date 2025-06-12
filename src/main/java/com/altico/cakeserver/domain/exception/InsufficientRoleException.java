package com.altico.cakeserver.domain.exception;

public class InsufficientRoleException extends RuntimeException {
    public InsufficientRoleException(String requiredRole) {
        super("Rol insuficiente. Se requiere: " + requiredRole);
    }
}
