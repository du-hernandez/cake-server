package com.altico.cakeserver.domain.exception;

public class ConcurrentSessionException extends RuntimeException {
    public ConcurrentSessionException(String username) {
        super("El usuario " + username + " ya tiene una sesión activa");
    }
}
