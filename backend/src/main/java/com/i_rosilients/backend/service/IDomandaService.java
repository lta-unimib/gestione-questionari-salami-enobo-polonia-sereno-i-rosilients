package com.i_rosilients.backend.service;

import java.io.IOException;
import java.util.List;

import com.i_rosilients.backend.dto.DomandaDTO;

public interface IDomandaService{

    void creaDomanda(DomandaDTO domandaDTO) throws IOException;

    void deleteDomanda(int idDomanda);

    void updateDomanda(int idDomanda, DomandaDTO domandaDTO);

    List<DomandaDTO> getDomandeByUtente(String emailUtente);

    List<DomandaDTO> getTutteLeDomande();
    
    public List<DomandaDTO> getDomandeByQuestionario(String idQuestionario);

}
