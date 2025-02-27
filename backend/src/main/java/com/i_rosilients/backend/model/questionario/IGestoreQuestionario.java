package com.i_rosilients.backend.model.questionario;

import java.util.List;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.dto.QuestionarioDTO;

public interface IGestoreQuestionario {
    public void creaQuestionario(QuestionarioDTO questionarioDTO);
    public void deleteQuestionario(int idQuestionario);
    public void updateQuestionario(int idQuestionario, QuestionarioDTO questionarioDTO);
    public List<QuestionarioDTO> getQuestionariByUtente(String emailUtente);
    public List<QuestionarioDTO> searchQuestionariWithQuestions(String nome);
    public List<QuestionarioDTO> getTuttiIQuestionari();
    public List<DomandaDTO> getDomandeByQuestionario(int idQuestionario);
    public QuestionarioDTO getQuestionario(int idQuestionario);
}
