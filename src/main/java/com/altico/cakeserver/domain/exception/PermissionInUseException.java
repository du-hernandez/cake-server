package com.altico.cakeserver.domain.exception;

public class PermissionInUseException extends RuntimeException {
    public PermissionInUseException(String nombre) {
        super("No se puede eliminar el permiso '" + nombre + "' porque está siendo utilizado");
    }
}
