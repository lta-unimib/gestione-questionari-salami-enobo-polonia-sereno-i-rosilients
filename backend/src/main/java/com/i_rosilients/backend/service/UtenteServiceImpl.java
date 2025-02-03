package com.i_rosilients.backend.service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;

@Service
public class UtenteServiceImpl implements UtenteService{

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Autowired
    // private EmailService emailService;

    // private final Map<String, String> tokenVerifica = new ConcurrentHashMap<>();//mappa per salvare temporaneamente i token

    // private String generaCodiceVerifica() {
    //     return String.valueOf(new Random().nextInt(900000) + 100000);
    // }

    @Override
    public Utente registraUtente(String email, String password) {
        /*
        if(utenteRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Utente già registrato");
        }
        */
        Utente nuovoUtente = new Utente();
        nuovoUtente.setEmail(email);
        nuovoUtente.setPassword(passwordEncoder.encode(password));
        nuovoUtente.setAttivo(false);
        return utenteRepository.save(nuovoUtente);
        /*
        String codiceVerifica = generaCodiceVerifica();

        tokenVerifica.put(email, codiceVerifica);

        emailService.inviaEmail(email, "Conferma registrazione", "Il tuo codice di verifica è: " + codiceVerifica);
         */
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
