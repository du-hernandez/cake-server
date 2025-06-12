package com.altico.cakeserver.domain.exception;

public class InvalidPermissionException extends RuntimeException {
    public InvalidPermissionException(String message) {
        super(message);
    }
}
