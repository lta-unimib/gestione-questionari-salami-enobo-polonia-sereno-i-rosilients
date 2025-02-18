package com.i_rosilients.backend.service;


import com.i_rosilients.backend.model.Risposta;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.repository.RispostaRepository;

import jakarta.transaction.Transactional;

import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.DomandaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RispostaService {

    @Autowired
    private RispostaRepository rispostaRepository;

    @Autowired
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    // Crea una nuova compilazione
    public int creaNuovaCompilazione(int idQuestionario) {
        Questionario questionario = questionarioRepository.findById(idQuestionario)
                .orElseThrow(() -> new RuntimeException("Questionario non trovato"));

        QuestionarioCompilato nuovaCompilazione = new QuestionarioCompilato();
        nuovaCompilazione.setQuestionario(questionario);
        nuovaCompilazione.setDataCompilazione(LocalDateTime.now()); // Imposta la data corrente

        QuestionarioCompilato compilazioneSalvata = questionarioCompilatoRepository.save(nuovaCompilazione);
        return compilazioneSalvata.getIdCompilazione(); // Restituisci l'ID della nuova compilazione
    }

    // Salva una risposta
    public void salvaRisposta(RispostaDTO rispostaDTO) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(rispostaDTO.getIdCompilazione())
                .orElseThrow(() -> new RuntimeException("QuestionarioCompilato non trovato"));

        Domanda domanda = domandaRepository.findById(rispostaDTO.getIdDomanda())
                .orElseThrow(() -> new RuntimeException("Domanda non trovata"));

        Risposta risposta = new Risposta();
        risposta.setQuestionarioCompilato(questionarioCompilato);
        risposta.setDomanda(domanda);
        risposta.setTestoRisposta(rispostaDTO.getTestoRisposta());

        rispostaRepository.save(risposta);
    }

}