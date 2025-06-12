package com.altico.cakeserver.domain.exception;

public class PasswordValidationException extends RuntimeException {
    public PasswordValidationException(String message) {
        super(message);
    }
}