package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestQuestionarioDTO {

    private QuestionarioDTO questionario;

    @BeforeEach
    public void setUp() {
        List<Integer> idDomande = new ArrayList<Integer>();
        idDomande.add(1);
        idDomande.add(2);
        idDomande.add(3);
        this.questionario = new QuestionarioDTO("provaQuest", "prova@mail", idDomande);
    }

    @Test
    void testGetArgomento() {
        assertEquals("provaQuest", questionario.getNome());
    }

    @Test
    void testGetTestoDomanda() {
        assertEquals("prova@mail", questionario.getEmailUtente());
    }

    @Test
    void testGetOpzioni() {
        Integer [] idTest = new Integer [] {1, 2, 3};
        assertArrayEquals(idTest, questionario.getIdDomande().toArray());
    }

}
