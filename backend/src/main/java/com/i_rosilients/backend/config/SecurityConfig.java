package com.i_rosilients.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilita CSRF (per test; in produzione valuta alternative)
                .cors(cors -> {}) // Abilita CORS, configura in un altro bean se necessario
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permetti richieste pre-flight CORS
                        .requestMatchers("/utente/login", "/utente/registrazione").permitAll() // Login e registrazione pubblici
                        .requestMatchers("/utente/info").authenticated() // Proteggi endpoint info
                        .anyRequest().authenticated() // Proteggi tutte le altre richieste
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Usa sessione se necessario
                );

        return http.build();
    }
}
