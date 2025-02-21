package com.i_rosilients.backend.controller;


import com.i_rosilients.backend.service.IUtenteService;
import com.i_rosilients.backend.model.Utente;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UtenteController {

    private final IUtenteService userService;

    public UtenteController(IUtenteService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Utente> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<Utente>> allUsers() {
        userService.allUsers();
        List <Utente> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
}
