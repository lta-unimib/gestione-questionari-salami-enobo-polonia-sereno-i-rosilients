package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.response.LoginResponse;
import com.i_rosilients.backend.response.VerificationResponse;
import com.i_rosilients.backend.service.AuthenticationService;
import com.i_rosilients.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Utente> register(@RequestBody UtenteDTO registerUtenteDto) {
        Utente registeredUtente = authenticationService.signup(registerUtenteDto);
        return ResponseEntity.ok(registeredUtente);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody UtenteDTO loginUtenteDto){
        Utente authenticatedUtente = authenticationService.authenticate(loginUtenteDto);
        String jwtToken = jwtService.generateToken(authenticatedUtente);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
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
}