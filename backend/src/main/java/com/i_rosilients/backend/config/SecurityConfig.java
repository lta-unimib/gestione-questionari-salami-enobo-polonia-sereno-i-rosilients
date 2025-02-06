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
                // Disabilita CSRF (solo per test; in produzione valuta soluzioni alternative)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Permetti le richieste pre-flight OPTIONS a tutti gli endpoint
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Consenti l'accesso agli endpoint pubblici
                        .requestMatchers("/utente/**", "/api/**").permitAll()
                        // Richiedi autenticazione per tutte le altre richieste
                        .anyRequest().authenticated()
                )
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Crea una sessione se necessaria
                    .maximumSessions(1) // Limita il numero di sessioni per utente
                    .expiredUrl("/login?expired=true"); // Redirigi a una pagina di login se la sessione scade

        return http.build();
    }
}
