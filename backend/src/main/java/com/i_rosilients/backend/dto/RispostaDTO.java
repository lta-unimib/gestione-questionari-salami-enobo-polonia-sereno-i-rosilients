package com.i_rosilients.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RispostaDTO {
    private int idCompilazione; // ID del QuestionarioCompilato
    private int idDomanda; // ID della Domanda
    private String testoRisposta; // Testo della risposta

    public RispostaDTO(int idDomanda, String testoRisposta) {
        this.idDomanda = idDomanda;
        this.testoRisposta = testoRisposta;
    }
   
}
