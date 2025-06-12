package com.altico.cakeserver.domain.exception;

public class SuspiciousActivityException extends RuntimeException {
    public SuspiciousActivityException(String message) {
        super(message);
    }
}
