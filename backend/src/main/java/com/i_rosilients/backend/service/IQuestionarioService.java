package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Questionario;

public interface IQuestionarioService {
    void creaQuestionario(QuestionarioDTO questionarioDTO);
    void deleteQuestionario(int idQuestionario);
    void updateQuestionario(int idQuestionario, QuestionarioDTO questionarioDTO);
    List<QuestionarioDTO> getQuestionariByUtente(String emailUtente);
    public List<Questionario> searchQuestionariWithQuestions(String nome);
}
