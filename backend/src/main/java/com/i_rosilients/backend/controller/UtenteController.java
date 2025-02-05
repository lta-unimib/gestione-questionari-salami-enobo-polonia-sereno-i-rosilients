package com.i_rosilients.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.service.IUtenteService;
import com.i_rosilients.backend.service.LoginMessage;
import com.i_rosilients.backend.service.UtenteService;

@RestController
@RequestMapping("/utente")
@CrossOrigin
public class UtenteController {

    @Autowired
    private IUtenteService utenteService;

    @PostMapping("/registrazione")
    public void registraUtente(@RequestBody UtenteDTO dto) {
        try {
            utenteService.registraUtente(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /* 
    @PostMapping("/login")
    public void loginUtente(@RequestBody UtenteDTO dto) {
        try {
            utenteService.registraUtente(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    } */

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginEmployee(@RequestBody UtenteDTO dto)
    {
        LoginMessage msg = utenteService.loginUtente(dto);
        return ResponseEntity.ok(msg);
    }


    /*
    @PostMapping("/verifica-email")
    public String verificaEmail(@RequestParam String email, @RequestParam String tokenInserito) {
        try {
            utenteService.verificaEmail(email, tokenInserito);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }    
        return "Email verificata con successo";
    }
        */
}
