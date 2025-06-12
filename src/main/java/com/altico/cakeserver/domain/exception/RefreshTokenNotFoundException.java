package com.altico.cakeserver.domain.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String tokenId) {
        super("Refresh token no encontrado: " + tokenId);
    }
}
