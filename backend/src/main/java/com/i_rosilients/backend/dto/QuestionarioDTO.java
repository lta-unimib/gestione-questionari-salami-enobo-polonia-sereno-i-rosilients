package com.i_rosilients.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // costruttore per restituire i questionari al frontend
public class QuestionarioDTO {
    
    private int idQuestionario;
    private String nome;
    private String emailUtente;

    // costruttore per la fetch di tutti i questionari
    public QuestionarioDTO(String nome, String emailUtente) {
        this.nome = nome;
        this.emailUtente = emailUtente;
    }
}
