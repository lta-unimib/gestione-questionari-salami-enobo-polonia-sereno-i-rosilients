package com.i_rosilients.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.DomandaQuestionario;
import com.i_rosilients.backend.model.Questionario;

import static org.junit.jupiter.api.Assertions.*;

 class DomandaQuestionarioTest {

    private DomandaQuestionario domandaQuestionario;

    @Mock
    private Domanda domanda;

    @Mock
    private Questionario questionario;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


        domandaQuestionario = new DomandaQuestionario();
        domandaQuestionario.setIdDomanda(1);
        domandaQuestionario.setIdQuestionario(2);
        domandaQuestionario.setDomanda(domanda);
        domandaQuestionario.setQuestionario(questionario);
    }

    @Test
    void testGetterAndSetter() {

        assertEquals(1, domandaQuestionario.getIdDomanda());
        assertEquals(2, domandaQuestionario.getIdQuestionario());

        domandaQuestionario.setIdDomanda(3);
        assertEquals(3, domandaQuestionario.getIdDomanda());

        domandaQuestionario.setIdQuestionario(4);
        assertEquals(4, domandaQuestionario.getIdQuestionario());
    }

    @Test
    void testRelazioniJPA() {

        assertEquals(domanda, domandaQuestionario.getDomanda());


        assertEquals(questionario, domandaQuestionario.getQuestionario());
    }

    
    @Test
    void testEqualsAndHashCode() {

        DomandaQuestionario domandaQuestionario1 = new DomandaQuestionario();
        domandaQuestionario1.setIdDomanda(1);
        domandaQuestionario1.setIdQuestionario(2);

        DomandaQuestionario domandaQuestionario2 = new DomandaQuestionario();
        domandaQuestionario2.setIdDomanda(1);
        domandaQuestionario2.setIdQuestionario(2);

        assertEquals(domandaQuestionario1, domandaQuestionario2);
        assertEquals(domandaQuestionario1.hashCode(), domandaQuestionario2.hashCode());
    }
}