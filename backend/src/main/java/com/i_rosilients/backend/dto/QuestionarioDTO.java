package com.i_rosilients.backend.dto;

import java.util.List;

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
    private List<Integer> idDomande;

    // costruttore per la fetch di tutti i questionari
    public QuestionarioDTO(String nome, String emailUtente, List<Integer> idDomande) {
        this.nome = nome;
        this.emailUtente = emailUtente;
        this.idDomande = idDomande;
    }
}
