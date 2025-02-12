package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.DomandaDTO;

public interface IDomandaService {
    void creaDomanda(DomandaDTO domandaDTO);
    void deleteDomanda(int idDomanda);
    List<DomandaDTO> getDomandeByUtente(String emailUtente);

}
