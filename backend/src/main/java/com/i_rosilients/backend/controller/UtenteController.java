package com.i_rosilients.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.i_rosilients.backend.service.UtenteService;

@RestController
@RequestMapping("/api/utente")
@CrossOrigin(origins = "http://localhost:3000")
public class UtenteController {
    
    private UtenteService utenteService;

    @PostMapping("/registrazione")
    public String registraUtente(@RequestParam String email, @RequestParam String password) {
        System.out.println("ciao");
        try {
            utenteService.registraUtente(email, password);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return "Utente registrato con successo";
    }

    @PostMapping("/verifica-email")
    public String verificaEmail(@RequestParam String email, @RequestParam String tokenInserito) {
        try {
            utenteService.verificaEmail(email, tokenInserito);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }    
        return "Email verificata con successo";
    }
}
