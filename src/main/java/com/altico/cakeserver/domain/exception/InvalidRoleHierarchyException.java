package com.altico.cakeserver.domain.exception;

public class InvalidRoleHierarchyException extends RuntimeException {
    public InvalidRoleHierarchyException(String message) {
        super(message);
    }
}
