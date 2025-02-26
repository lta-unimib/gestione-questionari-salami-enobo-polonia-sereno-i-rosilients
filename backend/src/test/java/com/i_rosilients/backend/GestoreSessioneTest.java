package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.utente.IGestoreUtente;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.session.AuthenticationService;
import com.i_rosilients.backend.services.session.GestoreSessione;
import com.i_rosilients.backend.services.session.JwtService;
import com.i_rosilients.backend.services.session.response.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GestoreSessioneTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private IGestoreUtente gestoreUtente;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private GestoreSessione gestoreSessione;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_Success() {
        // Arrange
        UtenteDTO utenteDTO = new UtenteDTO();
        Utente utente = new Utente();
        when(authenticationService.signup(any(UtenteDTO.class))).thenReturn(utente);

        // Act
        Utente result = gestoreSessione.signup(utenteDTO);

        // Assert
        assertNotNull(result);
        assertEquals(utente, result);
        verify(authenticationService, times(1)).signup(any(UtenteDTO.class));
    }

    @Test
    void authenticate_Success() {
        // Arrange
        UtenteDTO utenteDTO = new UtenteDTO();
        Utente authenticatedUtente = new Utente();
        when(authenticationService.authenticate(any(UtenteDTO.class))).thenReturn(authenticatedUtente);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        // Act
        LoginResponse result = gestoreSessione.authenticate(utenteDTO, response);

        // Assert
        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        assertEquals(3600L, result.getExpiresIn());
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void authenticate_UtenteNonTrovato() {
        // Arrange
        UtenteDTO utenteDTO = new UtenteDTO();
        when(authenticationService.authenticate(any(UtenteDTO.class))).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gestoreSessione.authenticate(utenteDTO, response));
    }

    @Test
    void refresh_Success() {
        // Arrange
        Cookie refreshCookie = new Cookie("refreshToken", "refreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");
        Utente utente = new Utente();
        when(authenticationService.findUtenteByEmail(anyString())).thenReturn(utente);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("newAccessToken");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        // Act
        LoginResponse result = gestoreSessione.refresh(request, response);

        // Assert
        assertNotNull(result);
        assertEquals("newAccessToken", result.getToken());
        assertEquals(3600L, result.getExpiresIn());
    }

    @Test
    void refresh_TokenExpiredOrNull() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gestoreSessione.refresh(request, response));
    }

    @Test
    void logout_Success() {
        // Act
        String result = gestoreSessione.logout(response);

        // Assert
        assertEquals("Logout effettuato con successo", result);
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void verifyUtente_Success() {
        // Arrange
        VerificaUtenteDTO verificaUtenteDTO = new VerificaUtenteDTO();
        doNothing().when(authenticationService).verifyUtente(any(VerificaUtenteDTO.class));

        // Act & Assert
        assertDoesNotThrow(() -> gestoreSessione.verifyUtente(verificaUtenteDTO));
    }

    @Test
    void resendVerificationCode_Success() {
        // Arrange
        String email = "test@example.com";
        doNothing().when(authenticationService).resendVerificationCode(anyString());

        // Act & Assert
        assertDoesNotThrow(() -> gestoreSessione.resendVerificationCode(email));
    }

    @Test
    void deleteProfile_Success() {
        // Arrange
        when(jwtService.extractToken(any(HttpServletRequest.class))).thenReturn("validToken");
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");
        when(jwtService.isTokenValid(anyString(), anyString())).thenReturn(true);
        Utente utente = new Utente();
        when(authenticationService.findUtenteByEmail(anyString())).thenReturn(utente);
        doNothing().when(gestoreUtente).deleteProfile(any(Utente.class));

        // Act
        String result = gestoreSessione.deleteProfile(request);

        // Assert
        assertEquals("Profilo eliminato con successo.", result);
    }

    @Test
    void deleteProfile_TokenExpiredOrNull() {
        // Arrange
        when(jwtService.extractToken(any(HttpServletRequest.class))).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gestoreSessione.deleteProfile(request));
    }

    @Test
    void deleteProfile_UtenteNonTrovato() {
        // Arrange
        when(jwtService.extractToken(any(HttpServletRequest.class))).thenReturn("validToken");
        when(jwtService.extractUsername(anyString())).thenReturn("test@example.com");
        when(jwtService.isTokenValid(anyString(), anyString())).thenReturn(true);
        when(authenticationService.findUtenteByEmail(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gestoreSessione.deleteProfile(request));
    }
}