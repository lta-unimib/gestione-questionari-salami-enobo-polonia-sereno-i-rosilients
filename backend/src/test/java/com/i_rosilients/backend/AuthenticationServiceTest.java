package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.EmailService;
import com.i_rosilients.backend.services.authentication.AuthenticationService;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UtenteRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
void testSignup_Success() throws MessagingException {

    UtenteDTO input = new UtenteDTO("test@example.com", "password");


    when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());


    when(passwordEncoder.encode(input.getPassword())).thenReturn("encodedPassword");


    doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());


    Utente savedUser = new Utente(input.getEmail(), "encodedPassword");
    savedUser.setVerificationCode("123456");
    savedUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
    savedUser.setEnabled(false);
    when(userRepository.save(any(Utente.class))).thenReturn(savedUser);


    Utente result = authenticationService.signup(input);


    assertNotNull(result); // Questo non dovrebbe più fallire
    assertEquals("test@example.com", result.getEmail());
    assertEquals("encodedPassword", result.getPassword());
    assertFalse(result.isEnabled());
    assertNotNull(result.getVerificationCode());
    assertNotNull(result.getVerificationCodeExpiresAt());


    verify(userRepository, times(1)).save(any(Utente.class));
}

    @Test
    void testSignup_EmailAlreadyRegistered() {

        UtenteDTO input = new UtenteDTO("test@example.com", "password");


        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(new Utente()));


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationService.signup(input);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("L'email è già registrata", exception.getReason());
    }

    @Test
    void testAuthenticate_Success() {

        UtenteDTO input = new UtenteDTO("test@example.com", "password");


        Utente user = new Utente("test@example.com", "encodedPassword");
        user.setEnabled(true);
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(user));


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);


        Utente result = authenticationService.authenticate(input);


        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
void testAuthenticate_InvalidCredentials() {

    UtenteDTO input = new UtenteDTO("test@example.com", "wrongPassword");


    Utente user = new Utente();
    user.setEmail("test@example.com");
    user.setEnabled(true); // Assicurati che l'utente sia abilitato

    when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(user));
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Credenziali errate"));


    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        authenticationService.authenticate(input);
    });

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Credenziali errate", exception.getReason());
}

    @Test
    void testVerifyUtente_Success() {

        VerificaUtenteDTO input = new VerificaUtenteDTO("test@example.com", "123456");


        Utente user = new Utente("test@example.com", "encodedPassword");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(user));


        authenticationService.verifyUtente(input);


        assertTrue(user.isEnabled());


        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testVerifyUtente_InvalidCode() {

        VerificaUtenteDTO input = new VerificaUtenteDTO("test@example.com", "wrongCode");


        Utente user = new Utente("test@example.com", "encodedPassword");
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(user));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.verifyUtente(input);
        });

        assertEquals("Invalid verification code", exception.getMessage());
    }

    @Test
    void testResendVerificationCode_Success() throws MessagingException {

        String email = "test@example.com";


        Utente user = new Utente(email, "encodedPassword");
        user.setEnabled(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());


        authenticationService.resendVerificationCode(email);


        assertNotNull(user.getVerificationCode());
        assertNotNull(user.getVerificationCodeExpiresAt());


        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testResendVerificationCode_AlreadyVerified() {

        String email = "test@example.com";


        Utente user = new Utente(email, "encodedPassword");
        user.setEnabled(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.resendVerificationCode(email);
        });

        assertEquals("Account is already verified", exception.getMessage());
    }

    @Test
    void testFindUtenteByEmail_Success() {

        String email = "test@example.com";


        Utente user = new Utente(email, "encodedPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        Utente result = authenticationService.findUtenteByEmail(email);


        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testFindUtenteByEmail_NotFound() {

        String email = "test@example.com";


        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.findUtenteByEmail(email);
        });

        assertEquals("Utente non trovato con email: " + email, exception.getMessage());
    }

    @Test
    void testDeleteProfile() {

        Utente utente = new Utente("test@example.com", "encodedPassword");


        doNothing().when(userRepository).delete(utente);


        authenticationService.deleteProfile(utente);


        verify(userRepository, times(1)).delete(utente);
    }
}