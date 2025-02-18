package com.i_rosilients.backend.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.RispostaRepository;

import jakarta.transaction.Transactional;

@Service
public class QuestionarioCompilatoService implements IQuestionarioCompilatoService {

    @Autowired
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Autowired
    private RispostaRepository rispostaRepository;
    
    @Transactional
    public void deleteQuestionarioCompilatoAndRisposte(Questionario questionario) {
        // Trova tutti i QuestionarioCompilato associati al Questionario
        List<QuestionarioCompilato> questionariCompilati =  questionarioCompilatoRepository.findByQuestionario(questionario);

        // Per ogni QuestionarioCompilato, elimina prima le Risposte associate
        for (QuestionarioCompilato questionarioCompilato : questionariCompilati) {
            rispostaRepository.deleteByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());
        }

        // Elimina tutti i QuestionarioCompilato associati
        questionarioCompilatoRepository.deleteByQuestionario(questionario);
    }
}
