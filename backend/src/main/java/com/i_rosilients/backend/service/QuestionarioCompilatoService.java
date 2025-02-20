package com.i_rosilients.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.model.Risposta;
import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.RispostaRepository;

import jakarta.transaction.Transactional;

@Service
public class QuestionarioCompilatoService implements IQuestionarioCompilatoService{

    @Autowired
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Autowired
    private RispostaRepository rispostaRepository;

    public List<QuestionarioCompilatoDTO> getCompilazioniInSospeso(String email) {
        List<QuestionarioCompilato> compilazioni = questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoFalse(email);
    
        return compilazioni.stream().map(compilazione -> {
            return new QuestionarioCompilatoDTO(
                compilazione.getIdCompilazione(),
                compilazione.getQuestionario().getIdQuestionario(),
                compilazione.getQuestionario().getNome(),
                compilazione.getQuestionario().getUtente().getEmail(),
                compilazione.getDataCompilazione(),
                new ArrayList<>()
            );
        }).collect(Collectors.toList());
    }

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

    public QuestionarioCompilatoDTO getQuestionarioCompilatoById(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione)
            .orElse(null);
    
        if (questionarioCompilato == null) {
            System.out.println("❌ Nessun questionario compilato trovato per ID: " + idCompilazione);
            return null;
        }
    
        List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(idCompilazione);
        System.out.println("✅ Numero risposte trovate: " + risposte.size());
    
        return new QuestionarioCompilatoDTO(
            questionarioCompilato.getIdCompilazione(),
            questionarioCompilato.getQuestionario().getIdQuestionario(),
            questionarioCompilato.getQuestionario().getNome(),
            questionarioCompilato.getQuestionario().getUtente().getEmail(),
            questionarioCompilato.getDataCompilazione(),
            risposte.stream()
                .map(r -> new RispostaDTO(idCompilazione, r.getDomanda().getIdDomanda(), r.getTestoRisposta()))
                .collect(Collectors.toList())
        );
    }

}
