package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.service.QuestionarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionari")
public class QuestionarioController {
/*
    private final QuestionarioService questionarioService;

    public QuestionarioController(QuestionarioService questionarioService) {
        this.questionarioService = questionarioService;
    }

    @PostMapping
    public Questionario creaQuestionario(@RequestBody QuestionarioDTO questionarioDTO) {
        return questionarioService.creaQuestionario(questionarioDTO);
    }

    @GetMapping("/{emailUtente}")
    public List<QuestionarioDTO> getQuestionariByUtente(@PathVariable String emailUtente) {
        return questionarioService.getQuestionariByUtente(emailUtente);
    }*/
}
