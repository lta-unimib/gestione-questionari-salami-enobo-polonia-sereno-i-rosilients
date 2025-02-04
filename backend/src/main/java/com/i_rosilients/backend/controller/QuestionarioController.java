package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.service.IQuestionarioService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/questionari")
public class QuestionarioController {

    private IQuestionarioService questionarioService;

    @PostMapping("/creaQuestionario")
    public void creaQuestionario(@RequestBody QuestionarioDTO questionarioDTO) {
        try {
            questionarioService.creaQuestionario(questionarioDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{emailUtente}")
    public List<QuestionarioDTO> getQuestionariByUtente(@PathVariable String emailUtente) {
        return questionarioService.getQuestionariByUtente(emailUtente);
    }
}
