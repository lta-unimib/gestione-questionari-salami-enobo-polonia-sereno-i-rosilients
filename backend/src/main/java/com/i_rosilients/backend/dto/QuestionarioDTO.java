package com.i_rosilients.backend.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  
public class QuestionarioDTO {
    private int idQuestionario;
    private String nome;
    private String emailUtente;
    private List<Integer> idDomande;

    public QuestionarioDTO(int idQuestionario, String nome, String emailUtente, List<Integer> idDomande) {
        this.idQuestionario = idQuestionario;
        this.nome = nome;
        this.emailUtente = emailUtente;
        this.idDomande = idDomande;
    }

    public QuestionarioDTO(String nome, String emailUtente, List<Integer> idDomande) {
        this.nome = nome;
        this.emailUtente = emailUtente;
        this.idDomande = idDomande;
    }

    public QuestionarioDTO(int id, String nome, String email) {
        this.idQuestionario = id;
        this.nome = nome;
        this.emailUtente = email;
    }
}