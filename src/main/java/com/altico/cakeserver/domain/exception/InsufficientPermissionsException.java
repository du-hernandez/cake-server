package com.altico.cakeserver.domain.exception;

public class InsufficientPermissionsException extends RuntimeException {
    public InsufficientPermissionsException(String requiredPermission) {
        super("Permiso insuficiente. Se requiere: " + requiredPermission);
    }
}
