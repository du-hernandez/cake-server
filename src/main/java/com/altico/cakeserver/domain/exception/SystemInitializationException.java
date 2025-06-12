package com.altico.cakeserver.domain.exception;

public class SystemInitializationException extends RuntimeException {
    public SystemInitializationException(String message) {
        super(message);
    }

    public SystemInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
