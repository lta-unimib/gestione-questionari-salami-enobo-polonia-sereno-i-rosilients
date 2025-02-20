package com.i_rosilients.backend.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.service.QuestionarioCompilatoService;

@RestController
@RequestMapping("/api/questionariCompilati")
public class QuestionarioCompilatoController {

    @Autowired
    private QuestionarioCompilatoService questionarioCompilatoService;

    @GetMapping("/{idCompilazione}")
    public ResponseEntity<QuestionarioCompilatoDTO> getQuestionarioCompilato(@PathVariable int idCompilazione) {
        QuestionarioCompilatoDTO questionarioCompilatoDTO = questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione);
        return ResponseEntity.ok(questionarioCompilatoDTO);
    }

    @GetMapping("/utenteNonRegistrato/{idCompilazione}")
    public ResponseEntity<QuestionarioCompilatoDTO> getQuestionarioCompilatoNonRegistrato(@PathVariable int idCompilazione) {
        QuestionarioCompilatoDTO questionarioCompilatoDTO = questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione);
        if (questionarioCompilatoDTO == null || !questionarioCompilatoService.checkEmailUtenteIsNullForQuestionario(idCompilazione)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(questionarioCompilatoDTO);
    }

    @GetMapping("/checkIsDefinitivo/{idCompilazione}")
    public ResponseEntity<Boolean> checkIsDefinitivo(@PathVariable int idCompilazione) {
        return ResponseEntity.ok(questionarioCompilatoService.checkIsDefinitivo(idCompilazione));
    }

    @GetMapping("/utente/{userEmail}")
    public List<QuestionarioCompilatoDTO> getQuestionariCompilatiUtente(@PathVariable String userEmail) {
        return questionarioCompilatoService.getCompilazioniInSospeso(userEmail);  
    }
}