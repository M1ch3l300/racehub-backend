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

        // Leggi ALLOWED_ORIGINS da variabile d'ambiente
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            // Se la variabile è definita, usa i valori specificati
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        } else {
            // Fallback per sviluppo locale
            configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        }

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
