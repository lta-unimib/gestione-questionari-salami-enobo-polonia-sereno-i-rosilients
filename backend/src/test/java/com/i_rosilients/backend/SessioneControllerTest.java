package com.i_rosilients.backend;

import com.i_rosilients.backend.controller.SessioneController;
import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.session.IGestoreSessione;
import com.i_rosilients.backend.services.session.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessioneControllerTest {

    @Mock
    private IGestoreSessione gestoreSessione;

    @InjectMocks
    private SessioneController sessioneController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void register_Success() {
        UtenteDTO utenteDTO = new UtenteDTO();
        Utente utente = new Utente();
        utente.setEmail("test@example.com");

        when(gestoreSessione.signup(any(UtenteDTO.class))).thenReturn(utente);

        ResponseEntity<?> response = sessioneController.register(utenteDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(utente, response.getBody());
    }

    @Test
    void register_ResponseStatusException() {
        UtenteDTO utenteDTO = new UtenteDTO();
        when(gestoreSessione.signup(any(UtenteDTO.class))).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore"));

        ResponseEntity<?> response = sessioneController.register(utenteDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\": \"Errore\"}", response.getBody());
    }

    @Test
    void register_IllegalArgumentException() {
        UtenteDTO utenteDTO = new UtenteDTO();
        when(gestoreSessione.signup(any(UtenteDTO.class))).thenThrow(new IllegalArgumentException("Errore"));

        ResponseEntity<?> response = sessioneController.register(utenteDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\": \"Errore\"}", response.getBody());
    }

    @Test
    void register_InternalServerError() {
        UtenteDTO utenteDTO = new UtenteDTO();
        when(gestoreSessione.signup(any(UtenteDTO.class))).thenThrow(new RuntimeException());

        ResponseEntity<?> response = sessioneController.register(utenteDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\": \"Errore durante la registrazione\"}", response.getBody());
    }

    @Test
    void authenticate_Success() {
        UtenteDTO utenteDTO = new UtenteDTO();
        LoginResponse loginResponse = new LoginResponse("token", 3600); // Usa il costruttore corretto
        when(gestoreSessione.authenticate(any(UtenteDTO.class), any(HttpServletResponse.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = sessioneController.authenticate(utenteDTO, mock(HttpServletResponse.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
    }

    @Test
    void authenticate_InternalServerError() {
        UtenteDTO utenteDTO = new UtenteDTO();
        when(gestoreSessione.authenticate(any(UtenteDTO.class), any(HttpServletResponse.class))).thenThrow(new RuntimeException());

        ResponseEntity<LoginResponse> response = sessioneController.authenticate(utenteDTO, mock(HttpServletResponse.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void refresh_Success() {
        LoginResponse loginResponse = new LoginResponse("newToken", 1800); // Usa il costruttore corretto
        when(gestoreSessione.refresh(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = sessioneController.refresh(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
    }

    @Test
    void refresh_InternalServerError() {
        when(gestoreSessione.refresh(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenThrow(new RuntimeException());

        ResponseEntity<LoginResponse> response = sessioneController.refresh(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void logout_Success() {
        when(gestoreSessione.logout(any(HttpServletResponse.class))).thenReturn("Logout successful");

        ResponseEntity<String> response = sessioneController.logout(mock(HttpServletResponse.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody());
    }

    @Test
    void logout_IllegalArgumentException() {
        when(gestoreSessione.logout(any(HttpServletResponse.class))).thenThrow(new IllegalArgumentException("Errore"));

        ResponseEntity<String> response = sessioneController.logout(mock(HttpServletResponse.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\": \"Errore\"}", response.getBody());
    }

    @Test
    void verifyUtente_Success() {
        VerificaUtenteDTO verificaUtenteDTO = new VerificaUtenteDTO();
        doNothing().when(gestoreSessione).verifyUtente(any(VerificaUtenteDTO.class));

        ResponseEntity<?> response = sessioneController.verifyUtente(verificaUtenteDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void verifyUtente_RuntimeException() {
        VerificaUtenteDTO verificaUtenteDTO = new VerificaUtenteDTO();
        doThrow(new RuntimeException("Errore")).when(gestoreSessione).verifyUtente(any(VerificaUtenteDTO.class));

        ResponseEntity<?> response = sessioneController.verifyUtente(verificaUtenteDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void resendVerificationCode_Success() {
        String email = "test@example.com";
        doNothing().when(gestoreSessione).resendVerificationCode(anyString());

        ResponseEntity<?> response = sessioneController.resendVerificationCode(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Verification code sent", response.getBody());
    }

    @Test
    void resendVerificationCode_RuntimeException() {
        String email = "test@example.com";
        doThrow(new RuntimeException("Errore")).when(gestoreSessione).resendVerificationCode(anyString());

        ResponseEntity<?> response = sessioneController.resendVerificationCode(email);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Errore", response.getBody());
    }

    @Test
    void deleteProfile_Success() {
        when(gestoreSessione.deleteProfile(any(HttpServletRequest.class))).thenReturn("Profilo eliminato con successo");

        ResponseEntity<String> response = sessioneController.deleteProfile(mock(HttpServletRequest.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profilo eliminato con successo", response.getBody());
    }

    @Test
    void deleteProfile_InternalServerError() {
        when(gestoreSessione.deleteProfile(any(HttpServletRequest.class))).thenThrow(new RuntimeException("Errore"));

        ResponseEntity<String> response = sessioneController.deleteProfile(mock(HttpServletRequest.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Errore durante l'eliminazione del profilo: Errore", response.getBody());
    }
}