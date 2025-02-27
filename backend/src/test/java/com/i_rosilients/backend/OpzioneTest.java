package com.i_rosilients.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.Opzione;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class OpzioneTest {

    private Opzione opzione;

    @Mock
    private Domanda domanda;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


        opzione = new Opzione();
        opzione.setIdOpzione(1);
        opzione.setTestoOpzione("Testo dell'opzione");
        opzione.setDomanda(domanda);
    }

    @Test
    void testGetterAndSetter() {

        assertEquals(1, opzione.getIdOpzione());
        assertEquals("Testo dell'opzione", opzione.getTestoOpzione());

        opzione.setIdOpzione(2);
        assertEquals(2, opzione.getIdOpzione());

        opzione.setTestoOpzione("Nuovo testo dell'opzione");
        assertEquals("Nuovo testo dell'opzione", opzione.getTestoOpzione());
    }

    @Test
    void testRelazioneJPA() {

        assertEquals(domanda, opzione.getDomanda());


        Domanda nuovaDomanda = mock(Domanda.class);
        opzione.setDomanda(nuovaDomanda);
        assertEquals(nuovaDomanda, opzione.getDomanda());
    }

    @Test
    void testEqualsAndHashCode() {

        Opzione opzione1 = new Opzione();
        opzione1.setIdOpzione(1);
        opzione1.setTestoOpzione("Testo dell'opzione");

        Opzione opzione2 = new Opzione();
        opzione2.setIdOpzione(1);
        opzione2.setTestoOpzione("Testo dell'opzione");

        assertEquals(opzione1, opzione2);
        assertEquals(opzione1.hashCode(), opzione2.hashCode());
    }
}