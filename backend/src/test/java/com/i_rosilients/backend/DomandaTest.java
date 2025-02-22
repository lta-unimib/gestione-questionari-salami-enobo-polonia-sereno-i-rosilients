package com.i_rosilients.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.DomandaQuestionario;
import com.i_rosilients.backend.model.Opzione;
import com.i_rosilients.backend.model.Utente;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class DomandaTest {

    private Domanda domanda;

    @Mock
    private Utente utente;

    @Mock
    private Opzione opzione;

    @Mock
    private DomandaQuestionario domandaQuestionario;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


        domanda = new Domanda(utente, "Matematica", "Quanto fa 2 + 2?", "path/to/image");
    }

    @Test
    void testGetterAndSetter() {

        domanda.setIdDomanda(1);
        assertEquals(1, domanda.getIdDomanda());

        domanda.setArgomento("Fisica");
        assertEquals("Fisica", domanda.getArgomento());

        domanda.setTestoDomanda("Qual è la velocità della luce?");
        assertEquals("Qual è la velocità della luce?", domanda.getTestoDomanda());

        domanda.setImmaginePath("new/path/to/image");
        assertEquals("new/path/to/image", domanda.getImmaginePath());
    }

    @Test
    void testRelazioniJPA() {

        assertEquals(utente, domanda.getUtente());


        List<Opzione> opzioni = new ArrayList<>();
        opzioni.add(opzione);
        domanda.setOpzioni(opzioni);
        assertEquals(1, domanda.getOpzioni().size());
        assertEquals(opzione, domanda.getOpzioni().get(0));


        List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();
        domandeQuestionario.add(domandaQuestionario);
        domanda.setDomandeQuestionario(domandeQuestionario);
        assertEquals(1, domanda.getDomandeQuestionario().size());
        assertEquals(domandaQuestionario, domanda.getDomandeQuestionario().get(0));
    }

    @Test
    void testCostruttore() {

        assertEquals(utente, domanda.getUtente());
        assertEquals("Matematica", domanda.getArgomento());
        assertEquals("Quanto fa 2 + 2?", domanda.getTestoDomanda());
        assertEquals("path/to/image", domanda.getImmaginePath());
    }

    @Test
    void testCascadeRemove() {

        List<Opzione> opzioni = new ArrayList<>();
        opzioni.add(opzione);
        domanda.setOpzioni(opzioni);

        List<DomandaQuestionario> domandeQuestionario = new ArrayList<>();
        domandeQuestionario.add(domandaQuestionario);
        domanda.setDomandeQuestionario(domandeQuestionario);


        domanda.getOpzioni().clear();
        domanda.getDomandeQuestionario().clear();

        assertTrue(domanda.getOpzioni().isEmpty());
        assertTrue(domanda.getDomandeQuestionario().isEmpty());
    }
}