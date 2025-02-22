package com.i_rosilients.backend;

import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
 class DomandaRepositoryTest {

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    private Utente utente;
    private Domanda domanda;

    @BeforeEach
     void setUp() {
        utente = new Utente("test@example.com", "password123");
        utenteRepository.save(utente);
        domanda = new Domanda();
        domanda.setTestoDomanda("Test Domanda");
        domanda.setArgomento("Test Argomento");
        domanda.setUtente(utente);
        domandaRepository.save(domanda);
    }

    @Test
     void testFindByUtente() {
        List<Domanda> result = domandaRepository.findByUtente(utente);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domanda.getTestoDomanda(), result.get(0).getTestoDomanda());
        assertEquals(domanda.getArgomento(), result.get(0).getArgomento());
    }

    @Test
     void testFindAll() {
        List<Domanda> result = domandaRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domanda.getTestoDomanda(), result.get(0).getTestoDomanda());
        assertEquals(domanda.getArgomento(), result.get(0).getArgomento());
    }

    @Test
     void testDeleteAllByUtente() {
        domandaRepository.deleteAllByUtente(utente);
        List<Domanda> result = domandaRepository.findByUtente(utente);
        assertTrue(result.isEmpty());
    }

    @Test
     void testFindDomandeIdsByQuestionarioId() {
        List<Integer> result = domandaRepository.findDomandeIdsByQuestionarioId(1);
        assertTrue(result.isEmpty());
    }
}