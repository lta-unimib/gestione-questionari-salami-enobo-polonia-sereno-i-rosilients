package com.i_rosilients.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.questionarioCompilato.QuestionarioCompilato;
import com.i_rosilients.backend.model.risposta.Risposta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class RispostaTest {

    private Risposta risposta;

    @Mock
    private QuestionarioCompilato questionarioCompilato;

    @Mock
    private Domanda domanda;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        risposta = new Risposta();
        risposta.setIdRisposta(1);
        risposta.setQuestionarioCompilato(questionarioCompilato);
        risposta.setDomanda(domanda);
        risposta.setTestoRisposta("Testo della risposta");
    }

    @Test
    void testGetterAndSetter() {

        assertEquals(1, risposta.getIdRisposta());
        assertEquals(questionarioCompilato, risposta.getQuestionarioCompilato());
        assertEquals(domanda, risposta.getDomanda());
        assertEquals("Testo della risposta", risposta.getTestoRisposta());
        risposta.setIdRisposta(2);
        assertEquals(2, risposta.getIdRisposta());
        risposta.setTestoRisposta("Nuovo testo della risposta");
        assertEquals("Nuovo testo della risposta", risposta.getTestoRisposta());
    }

    @Test
    void testRelazioniJPA() {

        assertEquals(questionarioCompilato, risposta.getQuestionarioCompilato());
        assertEquals(domanda, risposta.getDomanda());
        QuestionarioCompilato nuovoQuestionarioCompilato = mock(QuestionarioCompilato.class);
        Domanda nuovaDomanda = mock(Domanda.class);
        risposta.setQuestionarioCompilato(nuovoQuestionarioCompilato);
        risposta.setDomanda(nuovaDomanda);
        assertEquals(nuovoQuestionarioCompilato, risposta.getQuestionarioCompilato());
        assertEquals(nuovaDomanda, risposta.getDomanda());
    }

    @Test
    void testCascadeRemove() {

        risposta.setQuestionarioCompilato(null);
        assertNull(risposta.getQuestionarioCompilato());
    }

    @Test
    void testEqualsAndHashCode() {

        Risposta risposta1 = new Risposta();
        risposta1.setIdRisposta(1);

        Risposta risposta2 = new Risposta();
        risposta2.setIdRisposta(1);

        assertEquals(risposta1, risposta2);
        assertEquals(risposta1.hashCode(), risposta2.hashCode());
    }
}