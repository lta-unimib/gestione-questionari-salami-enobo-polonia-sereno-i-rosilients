package com.i_rosilients.backend.model.risposta;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.questionarioCompilato.QuestionarioCompilato;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRisposta;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_compilazione", referencedColumnName = "idCompilazione")
    private QuestionarioCompilato questionarioCompilato;      

    @ManyToOne
    @JoinColumn(name = "id_domanda", referencedColumnName = "idDomanda")
    private Domanda domanda;

    @Column(nullable = false)
    private String testoRisposta;

}
