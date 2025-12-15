package com.racehub.config;

import com.racehub.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CONFIGURAZIONE CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Disabilita CSRF (non necessario per API REST stateless)
                .csrf(csrf -> csrf.disable())

                // Configurazione autorizzazioni
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - autenticazione NON richiesta
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // GET requests - pubblici (lettura dati)
                        .requestMatchers(HttpMethod.GET, "/api/pilots/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/championships/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/races/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/announcements/**").permitAll()

                        // POST/PUT/DELETE su Championships - solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/championships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/championships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/championships/**").hasRole("ADMIN")

                        // POST/PUT/DELETE su Races - solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/races/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/races/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/races/**").hasRole("ADMIN")

                        // POST/PUT/DELETE su Announcements - solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/announcements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/announcements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/announcements/**").hasRole("ADMIN")

                        // Pilots - ADMIN può tutto, PILOT può modificare solo i propri dati
                        .requestMatchers(HttpMethod.POST, "/api/pilots/**").hasAnyRole("ADMIN", "PILOT")
                        .requestMatchers(HttpMethod.PUT, "/api/pilots/**").hasAnyRole("ADMIN", "PILOT")
                        .requestMatchers(HttpMethod.DELETE, "/api/pilots/**").hasRole("ADMIN")

                        // Tutto il resto richiede autenticazione
                        .anyRequest().authenticated()
                )

                // Session management - STATELESS (usa JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authentication provider
                .authenticationProvider(authenticationProvider())

                // JWT Filter prima di UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Per H2 Console (rimuovi in produzione)
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
