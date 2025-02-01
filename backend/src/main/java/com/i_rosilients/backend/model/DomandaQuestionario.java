package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class DomandaQuestionario {

    @Id
    @Column(name = "id_domanda")
    private int idDomanda;

    @Id
    @Column(name = "id_questionario")
    private int idQuestionario;

    @ManyToOne
    @JoinColumn(name = "id_domanda", referencedColumnName = "idDomanda")
    private Domanda domanda;

    @ManyToOne
    @JoinColumn(name = "id_questionario", referencedColumnName = "idQuestionario")
    private Questionario questionario;
}
