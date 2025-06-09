package com.altico.cakeserver.domain.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException(String username) {
        super("Usuario bloqueado: " + username);
    }
}
