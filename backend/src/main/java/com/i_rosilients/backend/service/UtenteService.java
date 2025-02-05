package com.i_rosilients.backend.service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;

@Service
public class UtenteService implements IUtenteService{

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final Map<String, String> tokenVerifica = new ConcurrentHashMap<>();//mappa per salvare temporaneamente i token

    private String generaCodiceVerifica() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    @Override
    public void registraUtente(UtenteDTO dto) {

        Utente nuovoUtente = new Utente(dto.getEmail(), passwordEncoder.encode(dto.getPassword()), dto.isAttivo());
        if(utenteRepository.findByEmail(nuovoUtente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Utente già registrato");
        }
        
        utenteRepository.save(nuovoUtente);
        
        // String codiceVerifica = generaCodiceVerifica();

        // tokenVerifica.put(nuovoUtente.getEmail(), codiceVerifica);

        // emailService.inviaEmail(email, "Conferma registrazione", "Il tuo codice di verifica è: " + codiceVerifica);
         
    }

    @Override
    public LoginMessage loginUtente(UtenteDTO dto) {
        String msg = "";
        Utente utente = utenteRepository.findByEmail(dto.getEmail()).orElse(null);
        if (utente != null) {
            String password = dto.getPassword();
            String encodedPassword = utente.getPassword();
            Boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Utente>utenteOptional = utenteRepository.findByEmailAndPassword(dto.getEmail(), encodedPassword);
                if (utenteOptional.isPresent()) {
                    return new LoginMessage("Login Success", true);
                } else {
                    return new LoginMessage("Login Failed", false);
                }
            } else {
                return new LoginMessage("password Not Match", false);
            }
        }else {
            return new LoginMessage("Email not exits", false);
        }
    }

    /* 
    @Override
    public void verificaEmail(String email, String tokenInserito) {
        String tokenSalvato = tokenVerifica.get(email);

        if (tokenSalvato != null && tokenSalvato.equals(tokenInserito)) {

            Optional<Utente> utenteOptional = utenteRepository.findByEmail(email);

            if (utenteOptional.isPresent()) {
                Utente utente = utenteOptional.get();
                utente.setAttivo(true);
                utenteRepository.save(utente);

                tokenVerifica.remove(email);
            }
        } else {
            throw new IllegalArgumentException("Codice di verifica non valido");
        }      
    }
        */
     
}
