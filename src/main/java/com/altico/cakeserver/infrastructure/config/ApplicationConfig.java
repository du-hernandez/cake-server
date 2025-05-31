package com.altico.cakeserver.infrastructure.config;

import com.altico.cakeserver.CakeServerApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@ComponentScan(basePackageClasses = CakeServerApplication.class)
@EnableJpaRepositories(basePackages = "com.altico.cakeserver.infrastructure.adapters.output.persistence.repository")
@EntityScan(basePackages = "com.cesarlead.inventory.infrastructure.adapters.output.persistence.entity")
public class ApplicationConfig {
    // La configuración principal de la aplicación
    // Spring Boot auto-configurará la mayoría de los beans necesarios
}
