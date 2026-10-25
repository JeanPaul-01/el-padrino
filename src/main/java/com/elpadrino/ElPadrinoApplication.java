package com.elpadrino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Sistema de gestión integral de la Barbería "El Padrino".
 * Arquitectura: Java 17 + Spring Boot + Spring Data JPA + JSP/Bootstrap 5 + MySQL.
 */
@SpringBootApplication
public class ElPadrinoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ElPadrinoApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ElPadrinoApplication.class);
    }
}
