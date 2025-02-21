package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.response.LoginResponse;
import com.i_rosilients.backend.response.VerificationResponse;
import com.i_rosilients.backend.service.AuthenticationService;
import com.i_rosilients.backend.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody UtenteDTO registerUtenteDto) {
        try {
            Utente registeredUtente = authenticationService.signup(registerUtenteDto);
            return ResponseEntity.ok(registeredUtente);
        } catch (ResponseStatusException e) {  // Cattura l'eccezione specifica
            return ResponseEntity.status(e.getStatusCode()).body("{\"message\": \"" + e.getReason() + "\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Errore durante la registrazione\"}");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody UtenteDTO loginUtenteDto, HttpServletResponse response) {
        try {
            Utente authenticatedUtente = authenticationService.authenticate(loginUtenteDto);
    
            if (authenticatedUtente == null) {
                return ResponseEntity.status(401).body(null);  // Se l'utente non è stato autenticato
            }
    
            String accessToken = jwtService.generateToken((UserDetails) authenticatedUtente);
            String refreshToken = jwtService.generateRefreshToken((UserDetails) authenticatedUtente);
    
            // Crea il cookie
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true); // Abilita solo in HTTPS
            refreshCookie.setPath("/auth/refresh");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
    
            response.addCookie(refreshCookie);
    
            return ResponseEntity.ok(new LoginResponse(accessToken, jwtService.getExpirationTime()));
        } catch (Exception e) {
            e.printStackTrace();  // Stampa eventuali eccezioni
            return ResponseEntity.status(500).body(null);  // Risposta di errore generico
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        // Legge il refresh token dal cookie
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(401).body(null);
        }

        String email = jwtService.extractUsername(refreshToken);
        Utente utente = authenticationService.findUtenteByEmail(email);
        String newAccessToken = jwtService.generateToken((UserDetails) utente);

        return ResponseEntity.ok(new LoginResponse(newAccessToken, jwtService.getExpirationTime()));
    }

    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Cancella il cookie del refresh token impostandone la durata a 0
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(0);

        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logout effettuato con successo");
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUtente(@RequestBody VerificaUtenteDTO verifyUtenteDto) {
        try {
            authenticationService.verifyUtente(verifyUtenteDto);
            VerificationResponse responseMessage = new VerificationResponse("Account verified successfully");
            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            VerificationResponse responseMessage = new VerificationResponse(e.getMessage());
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }
    

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteProfile")
    public ResponseEntity<String> deleteProfile(HttpServletRequest request) {
        String token = jwtService.extractToken(request);
        String username = jwtService.extractUsername(token);
        
        if (token == null || !jwtService.isTokenValid(token, username)) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        String emailUtente = jwtService.extractUsername(token);
        Utente utente = authenticationService.findUtenteByEmail(emailUtente);

        if (utente == null) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        }
        
        try {

            authenticationService.deleteProfile(utente);
            
            return ResponseEntity.ok("Profilo eliminato con successo.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore durante l'eliminazione del profilo: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        ResponseEntity<?> response = authenticationService.requestPasswordReset(email);
        return response;
    }

    @PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
    String token = request.get("token");
    String newPassword = request.get("newPassword");

    if (token == null || newPassword == null) {
        return ResponseEntity.badRequest().body("Token e nuova password sono obbligatori");
    }

    try {
        authenticationService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password resettata con successo");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
}