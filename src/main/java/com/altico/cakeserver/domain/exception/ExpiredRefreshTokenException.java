package com.altico.cakeserver.domain.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException(String tokenId) {
        super("Refresh token expirado: " + tokenId);
    }
}