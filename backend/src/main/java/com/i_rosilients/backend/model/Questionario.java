package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;


@Data
@Entity
@NoArgsConstructor
public class Questionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idQuestionario;

    @ManyToOne
    @JoinColumn(name = "email_utente", referencedColumnName = "email")
    private Utente utente;

    @Column(length = 255, nullable = false)
    private String nome;
    
    @OneToMany(mappedBy = "questionario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();

    @OneToMany(mappedBy = "questionario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<QuestionarioCompilato> questionariCompilati = new ArrayList<>();

    public Questionario(Utente utente, String nome) {
        this.utente = utente;
        this.nome = nome;
    }
}
