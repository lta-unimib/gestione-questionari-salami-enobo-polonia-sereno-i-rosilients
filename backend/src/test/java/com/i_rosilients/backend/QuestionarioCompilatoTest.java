package com.i_rosilients.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.i_rosilients.backend.model.questionario.Questionario;
import com.i_rosilients.backend.model.questionarioCompilato.QuestionarioCompilato;
import com.i_rosilients.backend.model.utente.Utente;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class QuestionarioCompilatoTest {

    private QuestionarioCompilato questionarioCompilato;

    @Mock
    private Utente utente;

    @Mock
    private Questionario questionario;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


        questionarioCompilato = new QuestionarioCompilato();
        questionarioCompilato.setIdCompilazione(1);
        questionarioCompilato.setUtente(utente);
        questionarioCompilato.setQuestionario(questionario);
        questionarioCompilato.setDataCompilazione(LocalDateTime.now());
        questionarioCompilato.setDefinitivo(true);
    }

    @Test
    void testGetterAndSetter() {

        assertEquals(1, questionarioCompilato.getIdCompilazione());
        assertEquals(utente, questionarioCompilato.getUtente());
        assertEquals(questionario, questionarioCompilato.getQuestionario());
        assertNotNull(questionarioCompilato.getDataCompilazione());
        assertTrue(questionarioCompilato.isDefinitivo());
        questionarioCompilato.setIdCompilazione(2);
        assertEquals(2, questionarioCompilato.getIdCompilazione());
        questionarioCompilato.setDefinitivo(false);
        assertFalse(questionarioCompilato.isDefinitivo());
    }

    @Test
    void testRelazioniJPA() {

        assertEquals(utente, questionarioCompilato.getUtente());
        assertEquals(questionario, questionarioCompilato.getQuestionario());
        Utente nuovoUtente = mock(Utente.class);
        Questionario nuovoQuestionario = mock(Questionario.class);
        questionarioCompilato.setUtente(nuovoUtente);
        questionarioCompilato.setQuestionario(nuovoQuestionario);
        assertEquals(nuovoUtente, questionarioCompilato.getUtente());
        assertEquals(nuovoQuestionario, questionarioCompilato.getQuestionario());
    }

    @Test
    void testDataCompilazione() {

        LocalDateTime nuovaData = LocalDateTime.of(2023, 10, 1, 12, 0);
        questionarioCompilato.setDataCompilazione(nuovaData);
        assertEquals(nuovaData, questionarioCompilato.getDataCompilazione());
    }

    @Test
    void testDefinitivo() {

        questionarioCompilato.setDefinitivo(false);
        assertFalse(questionarioCompilato.isDefinitivo());

        questionarioCompilato.setDefinitivo(true);
        assertTrue(questionarioCompilato.isDefinitivo());
    }

    @Test
    void testEqualsAndHashCode() {

        QuestionarioCompilato questionarioCompilato1 = new QuestionarioCompilato();
        questionarioCompilato1.setIdCompilazione(1);

        QuestionarioCompilato questionarioCompilato2 = new QuestionarioCompilato();
        questionarioCompilato2.setIdCompilazione(1);

        assertEquals(questionarioCompilato1, questionarioCompilato2);
        assertEquals(questionarioCompilato1.hashCode(), questionarioCompilato2.hashCode());
    }
}