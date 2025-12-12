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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // GET requests - tutti possono leggere
                        .requestMatchers(HttpMethod.GET, "/api/pilots/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/championships/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/races/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/announcements/**").permitAll()

                        // POST/PUT/DELETE - solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/championships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/championships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/championships/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/races/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/races/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/races/**").hasRole("ADMIN")

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
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Per H2 Console
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
