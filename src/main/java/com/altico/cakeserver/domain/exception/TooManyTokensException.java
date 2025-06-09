package com.altico.cakeserver.domain.exception;

public class TooManyTokensException extends RuntimeException {
    public TooManyTokensException(String username) {
        super("El usuario " + username + " ha excedido el límite de tokens activos");
    }
}
