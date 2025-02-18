package com.i_rosilients.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.service.QuestionarioCompilatoService;

@RestController
@RequestMapping("/api/questionariCompilati")
@CrossOrigin(origins = "*")
public class QuestionarioCompilatoController {

    @Autowired
    private QuestionarioCompilatoService questionarioCompilatoService;

    @GetMapping("/{idCompilazione}")
    public ResponseEntity<QuestionarioCompilatoDTO> getQuestionarioCompilato(@PathVariable int idCompilazione) {
        QuestionarioCompilatoDTO questionarioCompilatoDTO = questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione);
        return ResponseEntity.ok(questionarioCompilatoDTO);
    }
}