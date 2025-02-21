package com.i_rosilients.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RispostaDTO {
    private int idCompilazione; 
    private int idDomanda; 
    private String testoRisposta; 

    public RispostaDTO(int idDomanda, String testoRisposta) {
        this.idDomanda = idDomanda;
        this.testoRisposta = testoRisposta;
    }
   
}
