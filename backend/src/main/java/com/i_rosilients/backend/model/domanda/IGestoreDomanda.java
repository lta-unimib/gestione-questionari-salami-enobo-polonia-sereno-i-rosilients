package com.i_rosilients.backend.model.domanda;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.i_rosilients.backend.dto.DomandaDTO;

public interface IGestoreDomanda{

    public void creaDomanda(DomandaDTO domandaDTO) throws IOException;

    public void deleteDomanda(int idDomanda);

    public void updateDomanda(int idDomanda, DomandaDTO domandaDTO);

    public List<DomandaDTO> getDomandeByUtente(String emailUtente);

    public List<DomandaDTO> getTutteLeDomande();
    
    public List<DomandaDTO> getDomandeByQuestionario(String idQuestionario);

    public List<String> parseOpzioniJson(String opzioniJson) throws JsonProcessingException;

    public void eliminaImmagine(String imagePath) throws IOException;

    public String salvaImmagine(MultipartFile file) throws IOException;

    public ResponseEntity<Resource> getImage(String filename) throws IOException;

}
