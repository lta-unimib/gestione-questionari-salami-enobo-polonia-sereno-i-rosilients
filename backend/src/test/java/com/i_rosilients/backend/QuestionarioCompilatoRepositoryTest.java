package com.i_rosilients.backend;

import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class QuestionarioCompilatoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionarioCompilatoRepository repository;

    private Questionario questionario;
    private Utente utente;
    private QuestionarioCompilato questionarioCompilato;

    @BeforeEach
    void setUp() {

        utente = new Utente();
        utente.setEmail("test@example.com");
        utente.setPassword("password");
        entityManager.persist(utente);

        questionario = new Questionario();
        questionario.setNome("Test Questionario");
        entityManager.persist(questionario);

        questionarioCompilato = new QuestionarioCompilato();
        questionarioCompilato.setQuestionario(questionario);
        questionarioCompilato.setUtente(utente);
        questionarioCompilato.setDefinitivo(false);
        questionarioCompilato.setDataCompilazione(LocalDateTime.now());
        entityManager.persist(questionarioCompilato);
        entityManager.flush();
    }

    @Test
    void deleteByQuestionario() {
        assertNotNull(entityManager.find(QuestionarioCompilato.class, questionarioCompilato.getIdCompilazione()));
        repository.deleteByQuestionario(questionario);
        entityManager.flush();
        assertNull(entityManager.find(QuestionarioCompilato.class, questionarioCompilato.getIdCompilazione()));
    }

    @Test
    void deleteByIdCompilazione() {
        assertNotNull(entityManager.find(QuestionarioCompilato.class, questionarioCompilato.getIdCompilazione()));
        repository.deleteByIdCompilazione(questionarioCompilato.getIdCompilazione());
        entityManager.flush();
        assertNull(entityManager.find(QuestionarioCompilato.class, questionarioCompilato.getIdCompilazione()));
    }

    @Test
    void findByQuestionario() {
        List<QuestionarioCompilato> found = repository.findByQuestionario(questionario);
        assertFalse(found.isEmpty());
        assertEquals(questionario.getIdQuestionario(), found.get(0).getQuestionario().getIdQuestionario());
    }

    @Test
    void findByIdCompilazione() {
        Optional<QuestionarioCompilato> found = repository.findByIdCompilazione(questionarioCompilato.getIdCompilazione());
        assertTrue(found.isPresent());
        assertEquals(questionarioCompilato.getIdCompilazione(), found.get().getIdCompilazione());
    }

    @Test
    void findByUtenteEmailAndDefinitivoFalse() {
        List<QuestionarioCompilato> found = repository.findByUtenteEmailAndDefinitivoFalse(utente.getEmail());
        assertFalse(found.isEmpty());
        assertEquals(utente.getEmail(), found.get(0).getUtente().getEmail());
        assertFalse(found.get(0).isDefinitivo());
    }

    @Test
    void findByUtenteEmailAndDefinitivoTrue() {
        QuestionarioCompilato definitivo = new QuestionarioCompilato();
        definitivo.setQuestionario(questionario);
        definitivo.setUtente(utente);
        definitivo.setDefinitivo(true);
        definitivo.setDataCompilazione(LocalDateTime.now());
        entityManager.persist(definitivo);
        entityManager.flush();
        List<QuestionarioCompilato> found = repository.findByUtenteEmailAndDefinitivoTrue(utente.getEmail());
        assertFalse(found.isEmpty());
        assertEquals(utente.getEmail(), found.get(0).getUtente().getEmail());
        assertTrue(found.get(0).isDefinitivo());
    }

    @Test
    void findByUtenteEmailNotOrNullAndQuestionarioIdQuestionarioAndDefinitivo() {

        Utente altroUtente = new Utente();
        altroUtente.setEmail("altro@example.com");
        altroUtente.setPassword("password");
        entityManager.persist(altroUtente);

        QuestionarioCompilato altroQuestionarioCompilato = new QuestionarioCompilato();
        altroQuestionarioCompilato.setQuestionario(questionario);
        altroQuestionarioCompilato.setUtente(altroUtente);
        altroQuestionarioCompilato.setDefinitivo(true);
        altroQuestionarioCompilato.setDataCompilazione(LocalDateTime.now());
        entityManager.persist(altroQuestionarioCompilato);
        entityManager.flush();


        List<QuestionarioCompilato> found = repository.findByUtenteEmailNotOrNullAndQuestionarioIdQuestionarioAndDefinitivo(
            utente.getEmail(),
            questionario.getIdQuestionario()
        );
        assertFalse(found.isEmpty());
        assertNotEquals(utente.getEmail(), found.get(0).getUtente().getEmail());
        assertEquals(questionario.getIdQuestionario(), found.get(0).getQuestionario().getIdQuestionario());
        assertTrue(found.get(0).isDefinitivo());
    }
}
