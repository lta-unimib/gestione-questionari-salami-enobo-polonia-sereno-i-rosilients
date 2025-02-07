package com.i_rosilients.backend.service;

import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UtenteService {
    private final UtenteRepository userRepository;
    public UtenteService(UtenteRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    public List<Utente> allUsers() {
        List<Utente> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }
}
