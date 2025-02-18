package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.service.IQuestionarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/questionari")
public class QuestionarioController {

    @Autowired
    private IQuestionarioService questionarioService;


    @PostMapping("/creaQuestionario")
    public void creaQuestionario(@RequestBody QuestionarioDTO questionarioDTO) {
        try {
            questionarioService.creaQuestionario(questionarioDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/deleteQuestionario/{idQuestionario}")
    public void deleteQuestionario(@PathVariable int idQuestionario) {
        try {
            questionarioService.deleteQuestionario(idQuestionario);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updateQuestionario/{idQuestionario}")
    public void updateQuestionario(@PathVariable int idQuestionario, @RequestBody QuestionarioDTO questionarioDTO) {
        try {
            questionarioService.updateQuestionario(idQuestionario, questionarioDTO);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{emailUtente}")
    public List<QuestionarioDTO> getQuestionariByUtente(@PathVariable String emailUtente) {
        return questionarioService.getQuestionariByUtente(emailUtente);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuestionarioDTO>> searchQuestionari(@RequestParam String nome) {
        List<QuestionarioDTO> questionari = questionarioService.searchQuestionariWithQuestions(nome);
        return ResponseEntity.ok(questionari);
    }

    @GetMapping("/{id}/domande")
    public ResponseEntity<List<DomandaDTO>> getDomande(@PathVariable int id) {
        List<DomandaDTO> domande = questionarioService.getDomandeByQuestionario(id);
        return ResponseEntity.ok(domande);
    }
    
    @GetMapping("/{id}/view")
    public ResponseEntity<QuestionarioDTO> getQuestionarioWithDomande(@PathVariable int id) {
        QuestionarioDTO questionarioWithDomande = questionarioService.getQuestionario(id);
        return ResponseEntity.ok(questionarioWithDomande);
    }
}