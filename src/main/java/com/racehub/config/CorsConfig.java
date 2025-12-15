package com.racehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permetti richieste da localhost:3000 (React frontend)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Permetti tutti i metodi HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permetti tutti gli header
        configuration.setAllowedHeaders(List.of("*"));

        // Permetti credenziali (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Esponi header Authorization nella risposta
        configuration.setExposedHeaders(List.of("Authorization"));

        // Cache preflight request per 1 ora
        configuration.setMaxAge(3600L);

        // Applica configurazione a tutti gli endpoint
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
