package com.i_rosilients.backend.service;

import java.util.Map;

import com.i_rosilients.backend.dto.RispostaDTO;

public interface IRispostaService {

    public void salvaRisposta(RispostaDTO rispostaDTO);
    public void inviaEmailConPdf(String userEmail, int idCompilazione);
    public Map<Integer, String> getRisposteByIdCompilazione(int idCompilazione);
    public int creaNuovaCompilazione(int idQuestionario, String userEmail);
    public void finalizzaCompilazione(int idCompilazione);
}
