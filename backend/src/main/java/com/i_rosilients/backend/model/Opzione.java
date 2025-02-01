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
public class Opzione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOpzione;

    @ManyToOne
    @JoinColumn(name = "id_domanda", referencedColumnName = "idDomanda")
    private Domanda domanda;

    @Column(nullable = false)
    private String testoOpzione;

}
