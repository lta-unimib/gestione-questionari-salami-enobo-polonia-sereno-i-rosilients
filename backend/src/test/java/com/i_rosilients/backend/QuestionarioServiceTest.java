package com.i_rosilients.backend;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.*;
import com.i_rosilients.backend.repository.*;
import com.i_rosilients.backend.service.IQuestionarioCompilatoService;
import com.i_rosilients.backend.service.QuestionarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class QuestionarioServiceTest {

    @Mock
    private QuestionarioRepository questionarioRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private DomandaRepository domandaRepository;

    @Mock
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

    @Mock
    private IQuestionarioCompilatoService questionarioCompilatoService;

    @InjectMocks
    private QuestionarioService questionarioService;

    private Utente utente;
    private Questionario questionario;
    private QuestionarioDTO questionarioDTO;
    private Domanda domanda;

    @BeforeEach
    public void setUp() {
        utente = new Utente();
        utente.setEmail("test@example.com");

        questionario = new Questionario();
        questionario.setIdQuestionario(1);
        questionario.setNome("Test Questionario");
        questionario.setUtente(utente);

        questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Test Questionario");
        questionarioDTO.setEmailUtente("test@example.com");
        questionarioDTO.setIdDomande(Arrays.asList(1, 2));

        domanda = new Domanda();
        domanda.setIdDomanda(1);
        domanda.setTestoDomanda("Test Domanda");
        domanda.setUtente(utente);
    }

    @Test
     void testCreaQuestionario() {
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
        when(domandaRepository.existsById(1)).thenReturn(true);
        when(domandaRepository.existsById(2)).thenReturn(true);

        questionarioService.creaQuestionario(questionarioDTO);

        verify(questionarioRepository, times(1)).save(any(Questionario.class));
        verify(domandaQuestionarioRepository, times(2)).save(any(DomandaQuestionario.class));
    }

    @Test
    void testCreaQuestionario_UtenteNonTrovato() {
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            questionarioService.creaQuestionario(questionarioDTO);
        });

        assertEquals("Utente non trovato con email: test@example.com", exception.getMessage());
    }

    @Test
    void testDeleteQuestionario() {
        when(questionarioRepository.findById(1)).thenReturn(Optional.of(questionario));

        questionarioService.deleteQuestionario(1);

        verify(questionarioCompilatoService, times(1)).deleteQuestionarioCompilatoAndRisposte(questionario);
        verify(domandaQuestionarioRepository, times(1)).deleteByQuestionario(questionario);
        verify(questionarioRepository, times(1)).delete(questionario);
    }

    @Test
    void testDeleteQuestionario_NonTrovato() {
        when(questionarioRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            questionarioService.deleteQuestionario(1);
        });

        assertEquals("Questionario non trovato con id: 1", exception.getMessage());
    }

    @Test
    void testUpdateQuestionario() {
        when(questionarioRepository.findById(1)).thenReturn(Optional.of(questionario));
        when(domandaRepository.existsById(1)).thenReturn(true);
        when(domandaRepository.existsById(2)).thenReturn(true);

        questionarioService.updateQuestionario(1, questionarioDTO);

        verify(questionarioRepository, times(1)).save(questionario);
        verify(domandaQuestionarioRepository, times(1)).deleteByQuestionario(questionario);
        verify(domandaQuestionarioRepository, times(2)).save(any(DomandaQuestionario.class));
    }

    @Test
    void testGetQuestionariByUtente() {
        when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
        when(questionarioRepository.findByUtente(utente)).thenReturn(Collections.singletonList(questionario));
        when(domandaQuestionarioRepository.findByQuestionario(questionario)).thenReturn(Collections.emptyList());

        List<QuestionarioDTO> result = questionarioService.getQuestionariByUtente("test@example.com");

        assertEquals(1, result.size());
        assertEquals("Test Questionario", result.get(0).getNome());
    }

    @Test
    void testGetDomandeByQuestionario() {
        DomandaQuestionario domandaQuestionario = new DomandaQuestionario();
        domandaQuestionario.setDomanda(domanda);

        when(questionarioRepository.findById(1)).thenReturn(Optional.of(questionario));
        when(domandaQuestionarioRepository.findByQuestionario(questionario)).thenReturn(Collections.singletonList(domandaQuestionario));

        List<DomandaDTO> result = questionarioService.getDomandeByQuestionario(1);

        assertEquals(1, result.size());
        assertEquals("Test Domanda", result.get(0).getTestoDomanda());
    }

    @Test
    void testGetQuestionario() {
        when(questionarioRepository.findById(1)).thenReturn(Optional.of(questionario));

        QuestionarioDTO result = questionarioService.getQuestionario(1);

        assertEquals("Test Questionario", result.getNome());
        assertEquals("test@example.com", result.getEmailUtente());
    }
}