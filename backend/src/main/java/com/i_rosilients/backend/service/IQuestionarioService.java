package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.QuestionarioDTO;

public interface IQuestionarioService {
    void creaQuestionario(QuestionarioDTO questionarioDTO);
    void deleteQuestionario(int idQuestionario);
    List<QuestionarioDTO> getQuestionariByUtente(String emailUtente);
    
}
