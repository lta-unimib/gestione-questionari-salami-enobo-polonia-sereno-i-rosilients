package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.RispostaDTO;

public interface IRispostaService {

    public int creaNuovaCompilazione(int idQuestionario);
    public void salvaRisposta(RispostaDTO rispostaDTO);
}
