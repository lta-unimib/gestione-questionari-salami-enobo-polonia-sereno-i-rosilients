package com.i_rosilients.backend.model.risposta;

import java.util.Map;

import com.i_rosilients.backend.dto.RispostaDTO;

public interface IGestoreRisposta {

    public void salvaRisposta(RispostaDTO rispostaDTO);
    public void inviaEmailConPdf(String userEmail, int idCompilazione);
    public Map<Integer, String> getRisposteByIdCompilazione(int idCompilazione);
    public int creaNuovaCompilazione(int idQuestionario, String userEmail);
    public void finalizzaCompilazione(int idCompilazione);
}
