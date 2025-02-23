package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class QuestionarioCompilato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCompilazione;

    @ManyToOne
    @JoinColumn(name = "email_utente", referencedColumnName = "email", nullable = true)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "id_questionario", referencedColumnName = "idQuestionario")
    private Questionario questionario;

    @Column(nullable = false)
    private LocalDateTime dataCompilazione;

    @Column(nullable = false) // Nuovo campo per lo stato del questionario
    private boolean definitivo = false;
    
    @OneToMany(mappedBy = "questionarioCompilato", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Risposta> risposte = new ArrayList<>();
}
