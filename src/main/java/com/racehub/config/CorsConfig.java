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
            // Usa setAllowedOriginPatterns invece di setAllowedOrigins per supportare wildcard (es. https://*.mgx.dev)
            // Questo è necessario perché setAllowedOrigins non supporta wildcard quando allowCredentials è true
            configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        } else {
            // Fallback per sviluppo locale
            configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
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
