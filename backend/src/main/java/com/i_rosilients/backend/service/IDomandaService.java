package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.DomandaDTO;

public interface IDomandaService {
    void creaDomanda(DomandaDTO domandaDTO);
    List<DomandaDTO> getDomandeByUtente(String emailUtente);

}
