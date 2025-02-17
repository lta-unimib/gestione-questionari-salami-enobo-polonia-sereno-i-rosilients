package com.i_rosilients.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RispostaDTO {
    private int idCompilazione; // ID del QuestionarioCompilato
    private int idDomanda; // ID della Domanda
    private String testoRisposta; // Testo della risposta

   
}
