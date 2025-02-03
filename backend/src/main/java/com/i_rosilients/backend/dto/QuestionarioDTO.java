package com.i_rosilients.backend.dto;

import lombok.Data;

@Data
public class QuestionarioDTO {
    private int idQuestionario;
    private String nome;
    private String emailUtente;  // Evitiamo di inviare tutto l'oggetto Utente
}
