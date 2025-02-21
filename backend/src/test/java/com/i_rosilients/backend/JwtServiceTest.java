package com.i_rosilients.backend;


import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.i_rosilients.backend.service.JwtService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long jwtExpiration = 3600000; // 1 ora
    private long refreshTokenExpiration = 86400000; // 24 ore

    @BeforeEach
     void setUp() {
        jwtService = new JwtService();
        jwtService.setSecretKey(secretKey);
        jwtService.setJwtExpiration(jwtExpiration);
        jwtService.setRefreshTokenExpiration(refreshTokenExpiration);

        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
     void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
     void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
     void testGenerateTokenWithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        String token = jwtService.generateToken(extraClaims, userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
     void testGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
    }

    @Test
     void testIsTokenValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }
   
    @Test
     void testIsTokenExpired() {
        String token = jwtService.generateToken(userDetails);
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
     void testExtractExpiration() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
     void testExtractAllClaims() {
        String token = jwtService.generateToken(userDetails);
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
     void testIsTokenValid_WithUsername() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, "test@example.com");
        assertTrue(isValid);
    }

    @Test
     void testIsTokenValid_WithInvalidUsername() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, "wrong@example.com");
        assertFalse(isValid);
    }
}