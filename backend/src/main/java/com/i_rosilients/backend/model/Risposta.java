package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRisposta;

    @ManyToOne
    @JoinColumn(name = "id_compilazione", referencedColumnName = "idCompilazione")
    private QuestionarioCompilato questionarioCompilato;

    @ManyToOne
    @JoinColumn(name = "id_domanda", referencedColumnName = "idDomanda")
    private Domanda domanda;

    @Column(nullable = false)
    private String testoRisposta;

}
