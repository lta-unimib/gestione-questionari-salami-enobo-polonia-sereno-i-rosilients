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

    @DeleteMapping("/deleteDomanda/{idDomanda}")
    public void deleteQuestionario(@PathVariable int idDomanda) {
        try {
            domandaService.deleteDomanda(idDomanda);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updateDomanda/{idDomanda}")
    public void updateDomanda(@PathVariable int idDomanda, @RequestBody DomandaDTO domandaDTO) {
        try {
            domandaService.updateDomanda(idDomanda, domandaDTO);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{emailUtente}")
    public List<DomandaDTO> getDomandeByUtente(@PathVariable String emailUtente) {
        return domandaService.getDomandeByUtente(emailUtente);
    }


    //per ottenere tutte le domande
    @GetMapping("/tutteLeDomande")
    public List<DomandaDTO> getTutteLeDomande() {
        return domandaService.getTutteLeDomande();
    }
}