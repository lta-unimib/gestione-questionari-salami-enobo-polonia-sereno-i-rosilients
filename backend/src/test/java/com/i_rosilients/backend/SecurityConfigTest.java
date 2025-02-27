package com.i_rosilients.backend;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.i_rosilients.backend.config.JwtAuthenticationFilter;
import com.i_rosilients.backend.config.SecurityConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class SecurityConfigTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private HttpServletRequest mockRequest;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthenticationFilter, authenticationProvider);
    }

   @Test
void testCorsConfigurationSource() {

    HttpServletMapping mockMapping = mock(HttpServletMapping.class);
    when(mockRequest.getHttpServletMapping()).thenReturn(mockMapping);
    when(mockRequest.getRequestURI()).thenReturn("/api/test");
    when(mockRequest.getContextPath()).thenReturn("");
    when(mockRequest.getServletPath()).thenReturn("/api/test");  // Aggiunto questo mock

    CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

    CorsConfiguration actualConfig = corsConfigurationSource.getCorsConfiguration(mockRequest);
    
    assertNotNull(actualConfig);
    assertTrue(actualConfig.getAllowedOrigins().contains("http://localhost:3000"));
    assertTrue(actualConfig.getAllowedMethods().containsAll(
        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")));
    assertTrue(actualConfig.getAllowedHeaders().containsAll(
        List.of("Authorization", "Content-Type")));
    assertTrue(actualConfig.getAllowCredentials());
}

    @Test
    void testSecurityFilterChain() throws Exception {

        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mockFilterChain);

        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        assertNotNull(filterChain);
        verify(httpSecurity).cors(any());
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(authenticationProvider);
        verify(httpSecurity).addFilterBefore(eq(jwtAuthenticationFilter), any());
        verify(httpSecurity).exceptionHandling(any());
    }

    @Test
    void testConstructor() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, authenticationProvider);
        assertNotNull(config);
    }

    @Test
    void testAuthenticationProviderIsConfigured() throws Exception {

        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);
        

        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mockFilterChain);

        securityConfig.securityFilterChain(httpSecurity);


        verify(httpSecurity).authenticationProvider(eq(authenticationProvider));
    }
}