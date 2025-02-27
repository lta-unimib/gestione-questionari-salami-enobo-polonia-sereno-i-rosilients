package com.i_rosilients.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.i_rosilients.backend.model.questionario.DomandaQuestionario;
import com.i_rosilients.backend.model.questionario.Questionario;
import com.i_rosilients.backend.model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class QuestionarioTest {

    private Questionario questionario;

    @Mock
    private Utente utente;

    @Mock
    private DomandaQuestionario domandaQuestionario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        questionario = new Questionario(utente, "Questionario di Test");
    }

    @Test
    void testGetterAndSetter() {
        assertEquals("Questionario di Test", questionario.getNome());
        questionario.setIdQuestionario(1);
        assertEquals(1, questionario.getIdQuestionario());
        questionario.setNome("Nuovo Questionario");
        assertEquals("Nuovo Questionario", questionario.getNome());
    }

    @Test
    void testRelazioniJPA() {

        assertEquals(utente, questionario.getUtente());
        List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();
        domandeQuestionario.add(domandaQuestionario);
        questionario.setDomandeQuestionario(domandeQuestionario);
        assertEquals(1, questionario.getDomandeQuestionario().size());
        assertEquals(domandaQuestionario, questionario.getDomandeQuestionario().get(0));
    }

    @Test
    void testCostruttore() {
        assertEquals(utente, questionario.getUtente());
        assertEquals("Questionario di Test", questionario.getNome());
    }

    @Test
    void testCascadeRemove() {

        List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();
        domandeQuestionario.add(domandaQuestionario);
        questionario.setDomandeQuestionario(domandeQuestionario);
        questionario.getDomandeQuestionario().clear();
        assertTrue(questionario.getDomandeQuestionario().isEmpty());
    }
}
