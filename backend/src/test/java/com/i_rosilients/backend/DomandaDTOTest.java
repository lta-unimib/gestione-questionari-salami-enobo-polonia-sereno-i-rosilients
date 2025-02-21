package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.DomandaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomandaDTOTest {

    private DomandaDTO domanda;

    @BeforeEach
     void setUp() {
        List<String> opzioni = new ArrayList<String>();
        opzioni.add("opzione1");
        opzioni.add("opzione2");
        opzioni.add("opzione3");
        this.domanda = new DomandaDTO("provaArg", "provaTxt", "prova@mail", "api/domande/uploads/image.png", opzioni);
    }

    @Test
    void testGetArgomento() {
        assertEquals("provaArg", domanda.getArgomento());
    }

    @Test
    void testGetTestoDomanda() {
        assertEquals("provaTxt", domanda.getTestoDomanda());
    }

    @Test
    void testGetEmailUtente() {
        assertEquals("prova@mail", domanda.getEmailUtente());
    }

    @Test
    void testGetOpzioni() {
        String [] opzioni = new String [] {"opzione1", "opzione2", "opzione3"};
        assertArrayEquals(opzioni, domanda.getOpzioni().toArray());
    }

}
