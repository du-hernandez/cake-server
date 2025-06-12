package com.altico.cakeserver.domain.exception;

public class DataConsistencyException extends RuntimeException {
    public DataConsistencyException(String message) {
        super(message);
    }
}
