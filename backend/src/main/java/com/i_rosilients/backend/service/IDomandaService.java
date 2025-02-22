package com.i_rosilients.backend.service;

import java.io.IOException;
import java.util.List;

import com.i_rosilients.backend.dto.DomandaDTO;

public interface IDomandaService{

    public void creaDomanda(DomandaDTO domandaDTO) throws IOException;

    public void deleteDomanda(int idDomanda);

    public void updateDomanda(int idDomanda, DomandaDTO domandaDTO);

    public List<DomandaDTO> getDomandeByUtente(String emailUtente);

    public List<DomandaDTO> getTutteLeDomande();
    
    public List<DomandaDTO> getDomandeByQuestionario(String idQuestionario);

}
