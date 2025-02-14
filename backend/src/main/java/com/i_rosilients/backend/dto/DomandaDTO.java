package com.i_rosilients.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // costruttore per restituire i questionari al frontend
@NoArgsConstructor  // Aggiunto per permettere la deserializzazione di Jackson
public class DomandaDTO {
    private int idDomanda;
    private String argomento;
    private String testoDomanda;
    private String emailUtente;
    private List<String> opzioni;

    // costruttore per la fetch di tutti i questionari
    public DomandaDTO(String argomento, String testoDomanda, String emailUtente, List<String> opzioni) {
        this.argomento = argomento;
        this.testoDomanda = testoDomanda;
        this.emailUtente = emailUtente;
        this.opzioni = opzioni;
    }


}
