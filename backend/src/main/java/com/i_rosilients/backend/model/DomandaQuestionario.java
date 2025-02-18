package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
@IdClass(DomandaQuestionarioId.class)
public class DomandaQuestionario {

    @Id
    @Column(name = "id_domanda")
    private int idDomanda;

    @Id
    @Column(name = "id_questionario")
    private int idQuestionario;

    @ManyToOne
    @JoinColumn(name = "id_domanda", referencedColumnName = "idDomanda", insertable = false, updatable = false)
    private Domanda domanda;

    @ManyToOne
    @JoinColumn(name = "id_questionario", referencedColumnName = "idQuestionario", insertable = false, updatable = false)
    private Questionario questionario;
}
