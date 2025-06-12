package com.altico.cakeserver.domain.exception;

public class QuotaExceededException extends RuntimeException {
    public QuotaExceededException(String resource, long limit) {
        super("Cuota excedida para " + resource + ". Límite: " + limit);
    }
}
