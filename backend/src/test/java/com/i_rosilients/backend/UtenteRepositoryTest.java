package com.i_rosilients.backend;

import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
 class UtenteRepositoryTest {

    @Autowired
    private UtenteRepository utenteRepository;

    @Test
     void testFindByEmail() {
        Utente utente = new Utente("test@example.com", "password123");
        utenteRepository.save(utente);
        Optional<Utente> foundUtente = utenteRepository.findByEmail("test@example.com");
        assertTrue(foundUtente.isPresent());
        assertEquals("test@example.com", foundUtente.get().getEmail());
    }

    @Test
     void testFindByEmail_NotFound() {
        Optional<Utente> foundUtente = utenteRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUtente.isPresent());
    }

    @Test
     void testFindByVerificationCode() {
    Utente utente = new Utente("test@example.com", "password123");
    utente.setVerificationCode("123456");
    utenteRepository.save(utente);
    Optional<Utente> foundUtente = utenteRepository.findByVerificationCode("123456");
    assertTrue(foundUtente.isPresent());
    assertEquals("123456", foundUtente.get().getVerificationCode());
    }

    @Test
     void testFindByVerificationCode_NotFound() {
        Optional<Utente> foundUtente = utenteRepository.findByVerificationCode("nonexistent");
        assertFalse(foundUtente.isPresent());
    }

    @Test
     void testSaveUtente() {
    Utente utente = new Utente("newuser@example.com", "password123");
    Utente savedUtente = utenteRepository.save(utente);
    assertNotNull(savedUtente.getEmail());
    assertEquals("newuser@example.com", savedUtente.getEmail());
    }
}