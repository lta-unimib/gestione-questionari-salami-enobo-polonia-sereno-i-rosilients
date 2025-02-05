package com.i_rosilients.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor // costruttore per restituire i questionari al frontend
@NoArgsConstructor  // Aggiunto per permettere la deserializzazione di Jackson
public class DomandaDTO {
    private int idDomanda;
    private String argomento;
    private String testoDomanda;
    private String emailUtente;

    // costruttore per la fetch di tutti i questionari
    public DomandaDTO(String argomento, String testoDomanda, String emailUtente) {
        this.argomento = argomento;
        this.testoDomanda = testoDomanda;
        this.emailUtente = emailUtente;
    }
}
