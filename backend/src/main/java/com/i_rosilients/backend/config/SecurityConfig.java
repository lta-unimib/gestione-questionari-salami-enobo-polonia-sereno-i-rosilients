package com.i_rosilients.backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                // Abilita il supporto CORS con la configurazione globale definita in WebConfig
                .cors(withDefaults())
                // Disabilita CSRF (solo per test; in produzione valuta soluzioni alternative)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Permetti le richieste pre-flight OPTIONS a tutti gli endpoint
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Consenti l'accesso agli endpoint pubblici
                        .requestMatchers("/utente/**", "/api/**").permitAll()
                        // Richiedi autenticazione per tutte le altre richieste
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
