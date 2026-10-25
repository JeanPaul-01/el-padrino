package com.elpadrino.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AutenticacionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", "/reserva", "/reserva/**",   // acceso público: login y reserva en línea
                        "/app.css", "/vendor/**", "/*.html", "/favicon.ico", "/error");
    }
}
