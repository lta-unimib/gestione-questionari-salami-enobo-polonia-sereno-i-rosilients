package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Domanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDomanda;

    @ManyToOne
    @JoinColumn(name = "email_utente", referencedColumnName = "email", nullable = true)
    private Utente utente;

    @Column(nullable = false)
    private String argomento;

    @Column(nullable = false)
    private String testoDomanda;

    
    @OneToMany(mappedBy = "domanda", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Opzione> opzioni = new ArrayList<>();
    
    @OneToMany(mappedBy = "domanda", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();

    public Domanda(Utente utente, String argomento, String testoDomanda) {
        this.utente = utente;
        this.argomento = argomento;
        this.testoDomanda = testoDomanda;
    }

}
