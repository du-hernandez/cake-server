package com.altico.cakeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Esta configuración ya existe en el archivo ApplicationConfig (com.altico.cakeserver.infrastructure.config)
// @EnableJpaRepositories(basePackages = "com.altico.cakeserver.infrastructure.adapters.output.persistence.repository")
// @EntityScan(basePackages = "com.altico.cakeserver.infrastructure.adapters.output.persistence.entity")
@SpringBootApplication
public class CakeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CakeServerApplication.class, args);
    }
}
