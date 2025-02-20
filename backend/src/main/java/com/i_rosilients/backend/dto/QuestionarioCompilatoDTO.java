package com.i_rosilients.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionarioCompilatoDTO {
    private Integer idCompilazione;
    private Integer idQuestionario;
    private String titoloQuestionario;
    private String emailCreatore;
    private LocalDateTime dataCompilazione;
    private List<RispostaDTO> risposte;

    public QuestionarioCompilatoDTO(int idQuestionario, String titoloQuestionario, String emailCreatore, LocalDateTime dataCompilazione, List<RispostaDTO> risposte
    ) {
        this.idCompilazione = null;
        this.idQuestionario = idQuestionario;
        this.titoloQuestionario = titoloQuestionario;
        this.emailCreatore = emailCreatore;
        this.dataCompilazione = dataCompilazione;
        this.risposte = risposte;
    }
}