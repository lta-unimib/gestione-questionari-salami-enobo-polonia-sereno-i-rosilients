package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.DomandaQuestionario;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.DomandaQuestionarioRepository;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.QuestionarioRepository;
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
 class QuestionarioRepositoryTest {

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

    private Utente utente;
    private Questionario questionario;
    private Domanda domanda;
    private DomandaQuestionario domandaQuestionario;

    @BeforeEach
    void setUp() {
    // Crea un utente di test e salvalo nel database
    utente = new Utente("test@example.com", "password123");
    utenteRepository.save(utente); // Salva l'utente

    // Crea un questionario di test associato all'utente
    questionario = new Questionario(utente, "Test Questionario");
    questionarioRepository.save(questionario); // Salva il questionario

    // Crea una domanda di test
    domanda = new Domanda();
    domanda.setTestoDomanda("Test Domanda");
    domanda.setArgomento("Test Argomento");
    domanda.setUtente(utente);
    domandaRepository.save(domanda);

    // Crea una relazione DomandaQuestionario
    domandaQuestionario = new DomandaQuestionario();
    domandaQuestionario.setIdDomanda(domanda.getIdDomanda());
    domandaQuestionario.setIdQuestionario(questionario.getIdQuestionario());
    domandaQuestionario.setDomanda(domanda);
    domandaQuestionario.setQuestionario(questionario);
    domandaQuestionarioRepository.save(domandaQuestionario);
}

    @Test
     void testFindByUtente() {
        List<Questionario> result = questionarioRepository.findByUtente(utente);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(questionario.getNome(), result.get(0).getNome());
        assertEquals(utente.getEmail(), result.get(0).getUtente().getEmail());
    }

    @Test
     void testFindAll() {
        List<Questionario> result = questionarioRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(questionario.getNome(), result.get(0).getNome());
    }

    @Test
     void testSearchQuestionariWithQuestions() {
        List<QuestionarioDTO> result = questionarioRepository.searchQuestionariWithQuestions("Test");
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(questionario.getNome(), result.get(0).getNome());
        assertEquals(utente.getEmail(), result.get(0).getEmailUtente());
    }

    @Test
     void testFindDomandeIdsByQuestionarioId() {
        List<Integer> result = questionarioRepository.findDomandeIdsByQuestionarioId(questionario.getIdQuestionario());
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domanda.getIdDomanda(), result.get(0));
    }

   /* @Test
    public void testDeleteAllByUtente() {
    // Verifica che il questionario esista prima dell'eliminazione
    List<Questionario> questionariPrima = questionarioRepository.findByUtente(utente);
    assertFalse(questionariPrima.isEmpty()); // Verifica che ci sia almeno un questionario

    // Elimina tutti i questionari associati all'utente
    questionarioRepository.deleteAllByUtente(utente);

    // Verifica che il questionario sia stato eliminato
    List<Questionario> questionariDopo = questionarioRepository.findByUtente(utente);
    assertTrue(questionariDopo.isEmpty()); // Verifica che non ci siano più questionari
    }*/
}