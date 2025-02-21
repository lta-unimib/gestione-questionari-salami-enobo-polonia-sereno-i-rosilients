package com.i_rosilients.backend.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.UtenteRepository;
import com.i_rosilients.backend.response.VerificationResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    
    @Autowired
    private final UtenteRepository userRepository;
    @Autowired
    private PasswordResetService passwordResetService;
    
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UtenteRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Utente signup(UtenteDTO input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'email è già registrata"); 
        }
        Utente user = new Utente(input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public Utente authenticate(UtenteDTO input) {
        Utente user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    public void verifyUtente(VerificaUtenteDTO input) {
        Optional<Utente> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            Utente user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<Utente> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Utente user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(Utente user) { 
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public Utente findUtenteByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));  
    }

    @Transactional
    public void deleteProfile(Utente utente) {
        userRepository.delete(utente);
    }

    public ResponseEntity<?> requestPasswordReset(String email) {
        System.out.println("Richiesta di reset della password per l'email: " + email);
    
        Optional<Utente> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            System.out.println("Email non trovata: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utente non trovato"));
        }
    
        System.out.println("Utente trovato: " + userOptional.get().getEmail());
    
        
        String resetToken = passwordResetService.generateResetToken(email);
        System.out.println("Token generato: " + resetToken);
    
       
        String resetLink = "http://localhost:3000/reset-password/" + resetToken;
        String subject = "Reset della Password";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Reset della Password</h2>"
                + "<p style=\"font-size: 16px;\">Clicca sul link seguente per resettare la tua password:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<a href=\"" + resetLink + "\" style=\"font-size: 18px; font-weight: bold; color: #007bff;\">Resetta Password</a>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    
        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
            System.out.println("Email inviata con successo.");
            return ResponseEntity.ok(Map.of("message", "Email di reset inviata con successo"));
        } catch (MessagingException e) {
            System.out.println("Errore durante l'invio dell'email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante l'invio dell'email di reset"));
        }
    }

    public ResponseEntity<VerificationResponse> resetPassword(String token, String newPassword) {
        if (!passwordResetService.isResetTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new VerificationResponse("ERROR", "Token non valido o scaduto"));
        }
    
        String email = passwordResetService.getEmailFromResetToken(token);
        Utente user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utente non trovato"
                    );
                });
    
       
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetService.removeResetToken(token);
    
        return ResponseEntity.ok(new VerificationResponse("SUCCESS", "Password resettata con successo."));
    }

}