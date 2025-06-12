package com.altico.cakeserver.domain.exception;

public class RoleInUseException extends RuntimeException {
    public RoleInUseException(String nombre) {
        super("No se puede eliminar el rol '" + nombre + "' porque está siendo utilizado");
    }
}
