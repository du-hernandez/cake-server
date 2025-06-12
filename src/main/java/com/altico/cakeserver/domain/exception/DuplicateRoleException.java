package com.altico.cakeserver.domain.exception;

public class DuplicateRoleException extends RuntimeException {
    public DuplicateRoleException(String nombre) {
        super("Ya existe un rol con el nombre: " + nombre);
    }
}
