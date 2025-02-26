package com.i_rosilients.backend;

import com.i_rosilients.backend.controller.AuthenticationController;
import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.JwtService;
import com.i_rosilients.backend.services.authentication.AuthenticationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.i_rosilients.backend.model.utente.GestoreUtente;

@ExtendWith(MockitoExtension.class)
 class AutenticazioneControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private GestoreUtente gestoreUtente;

    @InjectMocks
    private AuthenticationController authenticationController;

    private Utente utente;
    private UtenteDTO utenteDTO;
    private VerificaUtenteDTO verificaUtenteDTO;

    @BeforeEach
     void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        utente = new Utente();
        utente.setEmail("test@example.com");
        utente.setPassword("password");

        utenteDTO = new UtenteDTO();
        utenteDTO.setEmail("test@example.com");
        utenteDTO.setPassword("password");

        verificaUtenteDTO = new VerificaUtenteDTO();
        verificaUtenteDTO.setEmail("test@example.com");
        verificaUtenteDTO.setVerificationCode("123456");
    }

    @Test
     void testRegister_Success() throws Exception {
        when(authenticationService.signup(any(UtenteDTO.class))).thenReturn(utente);

        mockMvc.perform(post("/auth/signup")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authenticationService, times(1)).signup(any(UtenteDTO.class));
    }

    @Test
     void testRegister_BadRequest() throws Exception {
        when(authenticationService.signup(any(UtenteDTO.class))).thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(post("/auth/signup")
                .contentType("application/json")
                .content("{\"email\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input"));

        verify(authenticationService, times(1)).signup(any(UtenteDTO.class));
    }

    @Test
     void testLogin_Success() throws Exception {
        // Configura i mock
        when(authenticationService.authenticate(any(UtenteDTO.class))).thenReturn(utente);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token"); // Simula la generazione del token
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken"); // Simula la generazione del refresh token
        when(jwtService.getExpirationTime()).thenReturn(3600L); // Simula il tempo di scadenza
    
        // Esegui la richiesta e verifica la risposta
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token")) // Verifica il campo token
                .andExpect(jsonPath("$.expiresIn").value(3600)) // Verifica il campo expiresIn
                .andExpect(jsonPath("$.message").value("Login successful")); // Verifica il campo message
    
        // Verifica che i metodi siano stati chiamati correttamente
        verify(authenticationService, times(1)).authenticate(any(UtenteDTO.class));
        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
        verify(jwtService, times(1)).generateRefreshToken(any(UserDetails.class));
        verify(jwtService, times(1)).getExpirationTime();
    }

    @Test
     void testLogin_Unauthorized() throws Exception {
        when(authenticationService.authenticate(any(UtenteDTO.class))).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());

        verify(authenticationService, times(1)).authenticate(any(UtenteDTO.class));
    }

    @Test
 void testRefresh_Success() throws Exception {
    // Configura i mock
    when(jwtService.extractUsername("refreshToken")).thenReturn("test@example.com");
    when(authenticationService.findUtenteByEmail("test@example.com")).thenReturn(utente);
    when(jwtService.generateToken(any(UserDetails.class))).thenReturn("newAccessToken");
    when(jwtService.getExpirationTime()).thenReturn(3600L);

    // Esegui la richiesta e verifica la risposta
    mockMvc.perform(post("/auth/refresh")
            .cookie(new Cookie("refreshToken", "refreshToken"))) // Includi il cookie di refresh
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("newAccessToken")) // Verifica il campo accessToken
            .andExpect(jsonPath("$.expiresIn").value(3600)); // Verifica il campo expiresIn

    // Verifica che i metodi siano stati chiamati correttamente
    verify(jwtService, times(1)).extractUsername("refreshToken");
    verify(authenticationService, times(1)).findUtenteByEmail("test@example.com");
    verify(jwtService, times(1)).generateToken(any(UserDetails.class));
    verify(jwtService, times(1)).getExpirationTime();
}

    @Test
     void testRefresh_Unauthorized() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
     void testLogout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout effettuato con successo"));
    }

    @Test
     void testVerifyUtente_Success() throws Exception {
        // Simula il comportamento del metodo void
        doNothing().when(authenticationService).verifyUtente(any(VerificaUtenteDTO.class));
    
        mockMvc.perform(post("/auth/verify")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"verificationCode\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account verified successfully"));
    
        // Verifica che il metodo sia stato chiamato
        verify(authenticationService, times(1)).verifyUtente(any(VerificaUtenteDTO.class));
    }

    @Test
 void testVerifyUtente_BadRequest() throws Exception {
    // Simula un'eccezione lanciata dal metodo void
    doThrow(new RuntimeException("Invalid code")).when(authenticationService).verifyUtente(any(VerificaUtenteDTO.class));

    mockMvc.perform(post("/auth/verify")
            .contentType("application/json")
            .content("{\"email\":\"test@example.com\",\"verificationCode\":\"wrong\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid code"));

    // Verifica che il metodo sia stato chiamato
    verify(authenticationService, times(1)).verifyUtente(any(VerificaUtenteDTO.class));
    }

    @Test
     void testResendVerificationCode_Success() throws Exception {
        doNothing().when(authenticationService).resendVerificationCode("test@example.com");

        mockMvc.perform(post("/auth/resend")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification code sent"));

        verify(authenticationService, times(1)).resendVerificationCode("test@example.com");
    }

    @Test
     void testResendVerificationCode_BadRequest() throws Exception {
        doThrow(new RuntimeException("Email not found")).when(authenticationService).resendVerificationCode("test@example.com");

        mockMvc.perform(post("/auth/resend")
                .param("email", "test@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email not found"));

        verify(authenticationService, times(1)).resendVerificationCode("test@example.com");
    }

    @Test
 void testDeleteProfile_Success() throws Exception {
    // Configura i mock
    when(jwtService.extractToken(any(HttpServletRequest.class))).thenReturn("validToken");
    when(jwtService.extractUsername("validToken")).thenReturn("test@example.com"); // Configura per due chiamate
    when(jwtService.isTokenValid("validToken", "test@example.com")).thenReturn(true); // Token valido
    when(authenticationService.findUtenteByEmail("test@example.com")).thenReturn(utente);
    doNothing().when(gestoreUtente).deleteProfile(utente);

    // Esegui la richiesta e verifica la risposta
    mockMvc.perform(delete("/auth/deleteProfile")
            .header("Authorization", "Bearer validToken")) // Includi l'header di autorizzazione
            .andExpect(status().isOk())
            .andExpect(content().string("Profilo eliminato con successo."));

    // Verifica che i metodi siano stati chiamati correttamente
    verify(jwtService, times(1)).extractToken(any(HttpServletRequest.class));
    verify(jwtService, times(2)).extractUsername("validToken"); // Verifica due chiamate
    verify(jwtService, times(1)).isTokenValid("validToken", "test@example.com");
    verify(authenticationService, times(1)).findUtenteByEmail("test@example.com");
    verify(gestoreUtente, times(1)).deleteProfile(utente);
}

    @Test
     void testDeleteProfile_Unauthorized() throws Exception {
        when(jwtService.extractToken(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(null);

        mockMvc.perform(delete("/auth/deleteProfile"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Utente non autenticato."));
    }

    @Test
 void testDeleteProfile_NotFound() throws Exception {
    // Configura i mock
    when(jwtService.extractToken(any(HttpServletRequest.class))).thenReturn("validToken");
    when(jwtService.extractUsername("validToken")).thenReturn("test@example.com"); // Configura per due chiamate
    when(jwtService.isTokenValid("validToken", "test@example.com")).thenReturn(true); // Token valido
    when(authenticationService.findUtenteByEmail("test@example.com")).thenReturn(null); // Utente non trovato

    // Esegui la richiesta e verifica la risposta
    mockMvc.perform(delete("/auth/deleteProfile")
            .header("Authorization", "Bearer validToken"))
            .andExpect(status().isNotFound()) // Verifica lo status 404
            .andExpect(content().string("Utente non trovato.")); // Verifica il messaggio di errore

    // Verifica che i metodi siano stati chiamati correttamente
    verify(jwtService, times(1)).extractToken(any(HttpServletRequest.class));
    verify(jwtService, times(2)).extractUsername("validToken"); // Verifica due chiamate
    verify(jwtService, times(1)).isTokenValid("validToken", "test@example.com");
    verify(authenticationService, times(1)).findUtenteByEmail("test@example.com");
    }
}