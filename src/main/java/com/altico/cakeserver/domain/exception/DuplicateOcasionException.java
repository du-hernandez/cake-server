package com.altico.cakeserver.domain.exception;

public class DuplicateOcasionException extends RuntimeException {
    public DuplicateOcasionException(String nombre) {
        super("Ya existe una ocasión con el nombre: " + nombre);
    }
}
