package com.i_rosilients.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor // costruttore per restituire i questionari al frontend
@NoArgsConstructor  // Aggiunto per permettere la deserializzazione di Jackson
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
