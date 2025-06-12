package com.altico.cakeserver.domain.exception;

public class TortaNotFoundException extends RuntimeException {
    public TortaNotFoundException(Integer id) {
        super("Torta con ID " + id + " no encontrada");
    }

    public TortaNotFoundException(String message) {
        super(message);
    }
}
