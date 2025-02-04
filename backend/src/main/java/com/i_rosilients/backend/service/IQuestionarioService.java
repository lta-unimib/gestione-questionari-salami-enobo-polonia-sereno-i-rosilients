package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.QuestionarioDTO;

public interface IQuestionarioService {
    void creaQuestionario(QuestionarioDTO questionarioDTO);
    List<QuestionarioDTO> getQuestionariByUtente(String emailUtente);
    
}
