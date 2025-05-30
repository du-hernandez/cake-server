package com.altico.cakeserver.domain.exception;

public class ImagenNotFoundException extends RuntimeException {
    public ImagenNotFoundException(Integer id) {
        super("Imagen con ID " + id + " no encontrada");
    }

    public ImagenNotFoundException(String message) {
        super(message);
    }
}
