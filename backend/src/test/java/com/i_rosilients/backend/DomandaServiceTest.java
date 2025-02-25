package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.GestoreDomanda;
import com.i_rosilients.backend.model.domanda.Opzione;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class DomandaServiceTest {

    @Mock
    private DomandaRepository domandaRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private OpzioneRepository opzioneRepository;

    @Mock
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

    @InjectMocks
    private GestoreDomanda domandaService;

    private Utente utente;
    private Domanda domanda;
    private DomandaDTO domandaDTO;

    @BeforeEach
     void setUp() {
        utente = new Utente();
        utente.setEmail("test@example.com");

        domanda = new Domanda();
        domanda.setIdDomanda(1);
        domanda.setArgomento("Test Argomento");
        domanda.setTestoDomanda("Test Domanda");
        domanda.setUtente(utente);

        domandaDTO = new DomandaDTO();
        domandaDTO.setArgomento("Test Argomento");
        domandaDTO.setTestoDomanda("Test Domanda");
        domandaDTO.setEmailUtente("test@example.com");
        domandaDTO.setOpzioni(Arrays.asList("Opzione 1", "Opzione 2"));
    }

    @Test
     void testCreaDomanda() throws IOException {
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
        when(domandaRepository.save(any(Domanda.class))).thenReturn(domanda);

        domandaService.creaDomanda(domandaDTO);

        verify(domandaRepository, times(1)).save(any(Domanda.class));
        verify(opzioneRepository, times(2)).save(any(Opzione.class));
    }

    @Test
     void testCreaDomanda_UtenteNonTrovato() {
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            domandaService.creaDomanda(domandaDTO);
        });

        assertEquals("Utente non trovato con email: test@example.com", exception.getMessage());
    }

    @Test
     void testUpdateDomanda() {
        when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));
        when(opzioneRepository.findByDomanda(domanda)).thenReturn(Collections.emptyList());

        domandaService.updateDomanda(1, domandaDTO);

        verify(domandaRepository, times(1)).save(domanda);
        verify(opzioneRepository, times(2)).save(any(Opzione.class));
    }

    @Test
     void testUpdateDomanda_DomandaNonTrovata() {
        when(domandaRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            domandaService.updateDomanda(1, domandaDTO);
        });

        assertEquals("Domanda non trovata con ID: 1", exception.getMessage());
    }

    @Test
     void testDeleteDomanda() {
        when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));

        domandaService.deleteDomanda(1);

        verify(domandaRepository, times(1)).delete(domanda);
    }

    @Test
     void testDeleteDomanda_DomandaNonTrovata() {
        when(domandaRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            domandaService.deleteDomanda(1);
        });

        assertEquals("Domanda non trovata con id: 1", exception.getMessage());
    }

    @Test
     void testGetTutteLeDomande() {
        when(domandaRepository.findAll()).thenReturn(Collections.singletonList(domanda));
        when(opzioneRepository.findByDomanda(domanda)).thenReturn(Collections.emptyList());

        List<DomandaDTO> result = domandaService.getTutteLeDomande();

        assertEquals(1, result.size());
        assertEquals("Test Argomento", result.get(0).getArgomento());
    }

    @Test
     void testGetDomandeByUtente() {
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
        when(domandaRepository.findByUtente(utente)).thenReturn(Collections.singletonList(domanda));
        when(opzioneRepository.findByDomanda(domanda)).thenReturn(Collections.emptyList());

        List<DomandaDTO> result = domandaService.getDomandeByUtente("test@example.com");

        assertEquals(1, result.size());
        assertEquals("Test Domanda", result.get(0).getTestoDomanda());
    }

    @Test
     void testGetDomandeByQuestionario() {
        when(domandaQuestionarioRepository.findDomandeIdsByQuestionarioId(1)).thenReturn(Collections.singletonList(1));
        when(domandaRepository.findAllById(Collections.singletonList(1))).thenReturn(Collections.singletonList(domanda));
        when(opzioneRepository.findByDomanda(domanda)).thenReturn(Collections.emptyList());

        List<DomandaDTO> result = domandaService.getDomandeByQuestionario("1");

        assertEquals(1, result.size());
        assertEquals("Test Argomento", result.get(0).getArgomento());
    }

    @Test
     void testGetDomandeByQuestionario_InvalidId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            domandaService.getDomandeByQuestionario("invalid");
        });

        assertEquals("ID del questionario non valido", exception.getMessage());
    }
}