package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.service.IDomandaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/domande")
public class DomandaController {

    @Autowired
    private IDomandaService domandaService;


    @PostMapping("/creaDomanda")
    public void creaDomanda(@RequestBody DomandaDTO domandaDTO) {
        try {
            domandaService.creaDomanda(domandaDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{emailUtente}")
    public List<DomandaDTO> getDomandeByUtente(@PathVariable String emailUtente) {
        return domandaService.getDomandeByUtente(emailUtente);
    }
}