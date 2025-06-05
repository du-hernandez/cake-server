package com.altico.cakeserver.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private final long expirationMs = 86400000;
    private final long refreshExpirationMs = 604800000;
    private String issuer;
}