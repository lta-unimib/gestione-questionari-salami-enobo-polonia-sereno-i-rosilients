package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;


@Getter
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

    public Questionario(Utente utente, String nome) {
        this.utente = utente;
        this.nome = nome;
    }
}
