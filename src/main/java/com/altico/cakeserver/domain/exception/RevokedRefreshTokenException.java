package com.altico.cakeserver.domain.exception;

public class RevokedRefreshTokenException extends RuntimeException {
    public RevokedRefreshTokenException(String tokenId) {
        super("Refresh token revocado: " + tokenId);
    }
}