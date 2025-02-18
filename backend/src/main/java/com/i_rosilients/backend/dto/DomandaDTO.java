package com.i_rosilients.backend.dto;

import java.io.File;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // costruttore per restituire i questionari al frontend
@NoArgsConstructor  // Aggiunto per permettere la deserializzazione di Jackson
public class DomandaDTO {
    private int idDomanda;
    private String argomento;
    private String testoDomanda;
    private String emailUtente;
    private String imagePath;
    private boolean removeImage;
    private List<String> opzioni;

    public DomandaDTO(String argomento, String testoDomanda, String emailUtente, String rawImagePath, List<String> opzioni) {
        this.argomento = argomento;
        this.testoDomanda = testoDomanda;
        this.emailUtente = emailUtente;
        this.imagePath = (rawImagePath != null && !rawImagePath.isEmpty()) 
                     ? "/api/domande/uploads/" + new File(rawImagePath).getName() 
                     : null;
        this.opzioni = opzioni;
        this.removeImage = false;
    }


}
