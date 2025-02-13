package com.i_rosilients.backend.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    
    @Autowired
    private final UtenteRepository userRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;
    
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
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
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

    private void sendVerificationEmail(Utente user) { //TODO: Update with company logo
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

    // Metodo per trovare un utente tramite email
    public Utente findUtenteByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));  
    }

    @Transactional
    public void removeUserFromRelatedEntities(Utente utente) {
    // Rimuovi l'utente dalle domande
    List<Domanda> domande = domandaRepository.findByUtente(utente);
    for (Domanda domanda : domande) {
        domanda.setUtente(null);  // Rimuove il riferimento all'utente
        domandaRepository.save(domanda);  // Salva la modifica
    }

    // Rimuovi l'utente dai questionari
    List<Questionario> questionari = questionarioRepository.findByUtente(utente);
    for (Questionario questionario : questionari) {
        questionario.setUtente(null);  // Rimuove il riferimento all'utente
        questionarioRepository.save(questionario);  // Salva la modifica
    }
}

    // Metodo per eliminare l'utente
    public void deleteProfile(Utente utente) {
        // Elimina prima le domande e i questionari associati
        domandaRepository.deleteAllByUtente(utente);
        questionarioRepository.deleteAllByUtente(utente);

        // Ora elimina l'utente
        userRepository.delete(utente);
    }

}