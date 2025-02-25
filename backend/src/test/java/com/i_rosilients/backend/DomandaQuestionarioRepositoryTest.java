package com.i_rosilients.backend;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.questionario.DomandaQuestionario;
import com.i_rosilients.backend.model.questionario.Questionario;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.DomandaQuestionarioRepository;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.QuestionarioRepository;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
 class DomandaQuestionarioRepositoryTest {

    @Autowired
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    private Questionario questionario;
    private Domanda domanda;
    private DomandaQuestionario domandaQuestionario;

    @BeforeEach
     void setUp() {
        Utente utente = new Utente("test@example.com", "password123");
        utenteRepository.save(utente);
        questionario = new Questionario(utente, "Test Questionario");
        questionarioRepository.save(questionario);
        domanda = new Domanda();
        domanda.setTestoDomanda("Test Domanda");
        domanda.setArgomento("Test Argomento");
        domandaRepository.save(domanda);
        domandaQuestionario = new DomandaQuestionario();
        domandaQuestionario.setIdDomanda(domanda.getIdDomanda());
        domandaQuestionario.setIdQuestionario(questionario.getIdQuestionario());
        domandaQuestionario.setDomanda(domanda);
        domandaQuestionario.setQuestionario(questionario);
        domandaQuestionarioRepository.save(domandaQuestionario);
    }

    @Test
     void testFindByQuestionario() {
        List<DomandaQuestionario> result = domandaQuestionarioRepository.findByQuestionario(questionario);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domandaQuestionario.getIdDomanda(), result.get(0).getIdDomanda());
        assertEquals(domandaQuestionario.getIdQuestionario(), result.get(0).getIdQuestionario());
    }

    @Test
     void testFindByDomanda() {
        List<DomandaQuestionario> result = domandaQuestionarioRepository.findByDomanda(domanda);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domandaQuestionario.getIdDomanda(), result.get(0).getIdDomanda());
        assertEquals(domandaQuestionario.getIdQuestionario(), result.get(0).getIdQuestionario());
    }

    @Test
     void testDeleteByQuestionario() {
        domandaQuestionarioRepository.deleteByQuestionario(questionario);
        List<DomandaQuestionario> result = domandaQuestionarioRepository.findByQuestionario(questionario);
        assertTrue(result.isEmpty());
    }

    @Test
     void testFindDomandeIdsByQuestionarioId() {
        List<Integer> result = domandaQuestionarioRepository.findDomandeIdsByQuestionarioId(questionario.getIdQuestionario());
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domanda.getIdDomanda(), result.get(0));
    }
}