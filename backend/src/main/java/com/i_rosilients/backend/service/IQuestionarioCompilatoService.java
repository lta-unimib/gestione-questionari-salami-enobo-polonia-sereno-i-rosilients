package com.i_rosilients.backend.service;

import java.util.List;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.Questionario;

public interface IQuestionarioCompilatoService {

   // elimina la compilazione e le risposte in base al questionario
   public void deleteQuestionarioCompilatoAndRisposte(Questionario questionario);

   // preleva la compilazione in base all'IdCompilazione
   public QuestionarioCompilatoDTO getQuestionarioCompilatoById(int idCompilazione);

   // Controlla se è definitivo
   public boolean checkIsDefinitivo(int idCompilazione);

   //metodi che ritornano le compilazioni in base a userEmail
   public List<QuestionarioCompilatoDTO> getCompilazioniInSospeso(String userEmail);

   public List<QuestionarioCompilatoDTO> getDefinitiviByUtente(String userEmail);

   public List<QuestionarioCompilatoDTO> getAllByUtente(String userEmail);

   // controlla se la compilazione è anonima
   public boolean checkEmailUtenteIsNullForQuestionario(int idCompilazione);

   // elimina la compilazione in base all'IdCompilazione
   public void deleteQuestionarioCompilatoAndRisposteByIdCompilazione(int idCompilazione);

   // preleva le risposte con IdCompilazione
   public List<RispostaDTO> getRisposteByCompilazione(int idCompilazione);
   
   // preleva le compilazioni in base all'IdQuestionario e userEmail
   public List<QuestionarioCompilatoDTO> getQuestionariCompilatiByUtenteAndIdQuestionario(String userEmail, int idQuestionario);

   // notifica l'utente con email della cancellazione di una compilazione
   public void inviaEmail(int idQuestionario, String userEmail);
}
