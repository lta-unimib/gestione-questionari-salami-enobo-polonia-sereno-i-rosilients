package com.i_rosilients.backend.model.utente;

import com.i_rosilients.backend.services.EmailService;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GestoreUtente implements IGestoreUtente {
    
    private final UtenteRepository userRepository;

    public GestoreUtente(UtenteRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Utente> allUsers() {
        List<Utente> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Transactional
    @Override
    public void deleteProfile(Utente utente) {
        userRepository.delete(utente);
    }
}
