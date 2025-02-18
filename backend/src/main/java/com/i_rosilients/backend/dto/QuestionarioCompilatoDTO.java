package com.i_rosilients.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionarioCompilatoDTO {
    private String titoloQuestionario;
    private String emailCreatore;
    private LocalDateTime dataCompilazione;
    private List<RispostaDTO> risposte;
}