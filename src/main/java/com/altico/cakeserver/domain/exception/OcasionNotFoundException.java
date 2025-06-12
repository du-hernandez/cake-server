package com.altico.cakeserver.domain.exception;

public class OcasionNotFoundException extends RuntimeException {
    public OcasionNotFoundException(Integer id) {
        super("Ocasión con ID " + id + " no encontrada");
    }

    public OcasionNotFoundException(String message) {
        super(message);
    }
}
