package com.i_rosilients.backend.model.domanda;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

import com.i_rosilients.backend.model.questionario.DomandaQuestionario;
import com.i_rosilients.backend.model.risposta.Risposta;
import com.i_rosilients.backend.model.utente.Utente;

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
    @JoinColumn(name = "email_utente", referencedColumnName = "email")
    private Utente utente;

    @Column(nullable = false)
    private String argomento;

    @Column(nullable = false)
    private String testoDomanda;

    @Column(name = "immagine_path")
    private String immaginePath;


    
    @OneToMany(mappedBy = "domanda", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Opzione> opzioni = new ArrayList<>();
    
    @OneToMany(mappedBy = "domanda", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();

    @OneToMany(mappedBy = "domanda", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Risposta> risposte = new ArrayList<>();

    public Domanda(Utente utente, String argomento, String testoDomanda, String immaginePath) {
        this.utente = utente;
        this.argomento = argomento;
        this.testoDomanda = testoDomanda;
        this.immaginePath = immaginePath;
    }

}
