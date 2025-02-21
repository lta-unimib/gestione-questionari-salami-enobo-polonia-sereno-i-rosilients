package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.model.Questionario;

public interface IQuestionarioCompilatoService {

   public void deleteQuestionarioCompilatoAndRisposte(Questionario questionario);
   public QuestionarioCompilatoDTO getQuestionarioCompilatoById(int idCompilazione);
   public boolean checkIsDefinitivo(int idCompilazione);
   public List<QuestionarioCompilatoDTO> getCompilazioniInSospeso(String email);
   public boolean checkEmailUtenteIsNullForQuestionario(int idCompilazione);
   
}
