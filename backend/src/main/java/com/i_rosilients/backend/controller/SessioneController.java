package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.services.session.IGestoreSessione;
import com.i_rosilients.backend.services.session.response.GenericResponse;
import com.i_rosilients.backend.services.session.response.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/auth")
@RestController
public class SessioneController {

    private final IGestoreSessione gestoreSessione;

    public SessioneController(IGestoreSessione gestoreSessione) {
        this.gestoreSessione = gestoreSessione;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody UtenteDTO registerUtenteDto) {
        try {
            
            return ResponseEntity.ok(gestoreSessione.signup(registerUtenteDto));
        } catch (ResponseStatusException e) {  
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
            return ResponseEntity.ok(gestoreSessione.authenticate(loginUtenteDto, response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        try{
            return ResponseEntity.ok(gestoreSessione.refresh(request, response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }  
    }

    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        try {
            return ResponseEntity.ok(gestoreSessione.logout(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUtente(@RequestBody VerificaUtenteDTO verifyUtenteDto) {
        try {
            gestoreSessione.verifyUtente(verifyUtenteDto);
            GenericResponse responseMessage = new GenericResponse("Account verified successfully");
            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            GenericResponse responseMessage = new GenericResponse(e.getMessage());
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }
    

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
         gestoreSessione.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/deleteProfile")
    public ResponseEntity<String> deleteProfile(HttpServletRequest request) { 
        try {
            return ResponseEntity.ok(gestoreSessione.deleteProfile(request));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore durante l'eliminazione del profilo: " + e.getMessage());
        }
    }

}