package com.i_rosilients.backend;

import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Opzione;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.OpzioneRepository;
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
 class OpzioneRepositoryTest {

    @Autowired
    private OpzioneRepository opzioneRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    private Domanda domanda;
    private Opzione opzione;

    @BeforeEach
     void setUp() {
        Utente utente = new Utente("test@example.com", "password123");
        utenteRepository.save(utente);
        domanda = new Domanda();
        domanda.setTestoDomanda("Test Domanda");
        domanda.setArgomento("Test Argomento");
        domanda.setUtente(utente);
        domandaRepository.save(domanda);
        opzione = new Opzione();
        opzione.setTestoOpzione("Test Opzione");
        opzione.setDomanda(domanda);
        opzioneRepository.save(opzione);
    }

    @Test
     void testFindByDomanda() {
        List<Opzione> result = opzioneRepository.findByDomanda(domanda);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(opzione.getTestoOpzione(), result.get(0).getTestoOpzione());
        assertEquals(domanda.getIdDomanda(), result.get(0).getDomanda().getIdDomanda());
    }

    @Test
     void testDeleteByDomanda() {
        opzioneRepository.deleteByDomanda(domanda);
        List<Opzione> result = opzioneRepository.findByDomanda(domanda);
        assertTrue(result.isEmpty());
    }
}