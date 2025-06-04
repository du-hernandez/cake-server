package com.altico.cakeserver.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        // Habilitar el lanzamiento de excepciones cuando no se encuentra un handler
//        // Deprecado en Spring Framework v6 (Spring Boot v3.x)
//        configurer.setUseTrailingSlashMatch(false);
//    }
}