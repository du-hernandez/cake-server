package com.altico.cakeserver.domain.exception;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String sessionId) {
        super("Sesión expirada: " + sessionId);
    }
}
