package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.model.Questionario;

public interface IQuestionarioCompilatoService {

   public void deleteQuestionarioCompilatoAndRisposte(Questionario questionario);

   public QuestionarioCompilatoDTO getQuestionarioCompilatoById(int idCompilazione);
}
