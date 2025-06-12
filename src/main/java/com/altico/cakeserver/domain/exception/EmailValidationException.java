package com.altico.cakeserver.domain.exception;

public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String email) {
        super("Email inválido: " + email);
    }
}
