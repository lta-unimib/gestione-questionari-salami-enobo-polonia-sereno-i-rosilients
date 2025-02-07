package com.i_rosilients.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

import jakarta.servlet.http.HttpServletRequest;

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

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUtente(@RequestBody UtenteDTO dto, HttpServletRequest request) {
        LoginMessage msg = utenteService.loginUtente(dto);

        // Se il login è riuscito
        if (msg.getStatus()) {
            // Memorizza l'utente nella sessione
            request.getSession().setAttribute("utente", dto); // Memorizza i dettagli dell'utente nella sessione
            return ResponseEntity.ok(msg);  // Restituisce OK con il messaggio
        } else {
            // Se il login è fallito (errore nelle credenziali)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);  // Restituisce Unauthorized con il messaggio di errore
        }
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Invalidare la sessione
        request.getSession().invalidate();
        return ResponseEntity.ok("Logout effettuato con successo.");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        UtenteDTO utente = (UtenteDTO) request.getSession().getAttribute("utente");
        if (utente != null) {
            return ResponseEntity.ok(utente);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato");
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
