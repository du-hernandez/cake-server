package com.altico.cakeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.altico.cakeserver.infrastructure.adapters.output.persistence.repository")
@EntityScan(basePackages = "com.altico.cakeserver.infrastructure.adapters.output.persistence.entity")
public class CakeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CakeServerApplication.class, args);
    }

}
