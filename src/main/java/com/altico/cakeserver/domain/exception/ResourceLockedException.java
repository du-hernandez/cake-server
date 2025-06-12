package com.altico.cakeserver.domain.exception;

public class ResourceLockedException extends RuntimeException {
    public ResourceLockedException(String resource) {
        super("Recurso bloqueado: " + resource);
    }
}
