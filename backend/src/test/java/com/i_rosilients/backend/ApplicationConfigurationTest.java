package com.i_rosilients.backend;

import com.i_rosilients.backend.config.ApplicationConfiguration;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
 class ApplicationConfigurationTest {

    @Mock
    private UtenteRepository utenteRepository;

    private ApplicationConfiguration applicationConfiguration;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationConfiguration = new ApplicationConfiguration(utenteRepository);
    }

    @Test
     void testUserDetailsService() {
        Utente utente = new Utente("test@example.com", "password123");
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
     void testUserDetailsService_UserNotFound() {
        when(utenteRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
     void testPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder = applicationConfiguration.passwordEncoder();
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(mockAuthManager);
        AuthenticationManager authenticationManager = applicationConfiguration.authenticationManager(authConfig);
        assertNotNull(authenticationManager);
    }

    @Test
    void testAuthenticationProvider() {
        AuthenticationProvider authenticationProvider = applicationConfiguration.authenticationProvider();
        assertNotNull(authenticationProvider);
        BCryptPasswordEncoder passwordEncoder = applicationConfiguration.passwordEncoder();
        String encodedPassword = passwordEncoder.encode("password123");
        Utente utente = new Utente("test@example.com", encodedPassword);
        utente.setEnabled(true); 
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@example.com", "password123");
        Authentication result = authenticationProvider.authenticate(authentication);
        assertNotNull(result);
    }
}
