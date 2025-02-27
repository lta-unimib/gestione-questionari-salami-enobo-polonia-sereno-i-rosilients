package com.i_rosilients.backend;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.questionarioCompilato.QuestionarioCompilato;
import com.i_rosilients.backend.model.risposta.Risposta;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.services.persistence.RispostaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
 class RispostaRepositoryTest {

    @Autowired
    private RispostaRepository rispostaRepository;

    @Autowired
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    private QuestionarioCompilato questionarioCompilato;
    private Domanda domanda;
    private Risposta risposta;

    @BeforeEach
     void setUp() {

        questionarioCompilato = new QuestionarioCompilato();
        questionarioCompilato.setDataCompilazione(LocalDateTime.now());
        questionarioCompilatoRepository.save(questionarioCompilato);


        domanda = new Domanda();
        domanda.setTestoDomanda("Test Domanda");
        domanda.setArgomento("Test Argomento");
        domandaRepository.save(domanda);


        risposta = new Risposta();
        risposta.setQuestionarioCompilato(questionarioCompilato);
        risposta.setDomanda(domanda);
        risposta.setTestoRisposta("Test Risposta");
        rispostaRepository.save(risposta);
    }

    @Test
     void testDeleteByQuestionarioCompilato_IdCompilazione() {

        rispostaRepository.deleteByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());


        List<Risposta> result = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());
        assertTrue(result.isEmpty());
    }

    @Test
     void testFindByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda() {

        Optional<Risposta> result = rispostaRepository.findByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(
                questionarioCompilato.getIdCompilazione(), domanda.getIdDomanda());


        assertTrue(result.isPresent());
        assertEquals(risposta.getTestoRisposta(), result.get().getTestoRisposta());
    }

    @Test
public void testDeleteByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda() {
    rispostaRepository.deleteByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(
            questionarioCompilato.getIdCompilazione(), domanda.getIdDomanda());
    Optional<Risposta> result = rispostaRepository.findByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(
            questionarioCompilato.getIdCompilazione(), domanda.getIdDomanda());
    assertFalse(result.isPresent());
}

    @Test
     void testFindByQuestionarioCompilato_IdCompilazione() {

        List<Risposta> result = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());


        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(risposta.getTestoRisposta(), result.get(0).getTestoRisposta());
    }

    @Test
     void testFindByQuestionarioCompilato() {

        List<Risposta> result = rispostaRepository.findByQuestionarioCompilato(questionarioCompilato);


        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(risposta.getTestoRisposta(), result.get(0).getTestoRisposta());
    }
}