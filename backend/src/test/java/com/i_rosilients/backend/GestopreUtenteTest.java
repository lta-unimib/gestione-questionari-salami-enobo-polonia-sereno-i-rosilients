package com.i_rosilients.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.i_rosilients.backend.model.utente.GestoreUtente;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GestopreUtenteTest {

    @Mock
    private UtenteRepository userRepository;

    @InjectMocks
    private GestoreUtente utenteService;

    private List<Utente> utenti;

    @BeforeEach
    void setup() {
        utenti = new ArrayList<>();
        Utente utente1 = new Utente();
        utente1.setEmail("utente1@example.com");

        Utente utente2 = new Utente();
        utente2.setEmail("utente2@example.com");

        utenti.add(utente1);
        utenti.add(utente2);
    }

    @Test
    void testAllUsers() {
        when(userRepository.findAll()).thenReturn(utenti);
        List<Utente> result = utenteService.allUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("utente1@example.com", result.get(0).getEmail());
        assertEquals("utente2@example.com", result.get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }
}