package com.i_rosilients.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.model.Risposta;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.RispostaRepository;
import com.i_rosilients.backend.service.QuestionarioCompilatoService;

@ExtendWith(MockitoExtension.class)
 class QuestionarioCompilatoServiceTest {

    @Mock
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Mock
    private RispostaRepository rispostaRepository;

    @InjectMocks
    private QuestionarioCompilatoService questionarioCompilatoService;

    private QuestionarioCompilato questionarioCompilato;
    private Questionario questionario;
    private Utente utente;
    private Risposta risposta;

    @BeforeEach
 void setup() {
  
    utente = new Utente();
    utente.setEmail("test@example.com");
    questionario = new Questionario();
    questionario.setIdQuestionario(1);
    questionario.setNome("Titolo Questionario");
    questionario.setUtente(utente);
    Domanda domanda = new Domanda();
    domanda.setIdDomanda(1);
    domanda.setArgomento("Argomento 1");
    domanda.setTestoDomanda("Domanda 1");
    domanda.setImmaginePath("/path/to/image");
    domanda.setUtente(utente); 
    questionarioCompilato = new QuestionarioCompilato();
    questionarioCompilato.setIdCompilazione(1);
    questionarioCompilato.setQuestionario(questionario);
    questionarioCompilato.setUtente(utente);
    questionarioCompilato.setDataCompilazione(LocalDateTime.now());
    questionarioCompilato.setDefinitivo(false);
    risposta = new Risposta();
    risposta.setIdRisposta(1);
    risposta.setDomanda(domanda); 
    risposta.setTestoRisposta("Risposta 1");
    risposta.setQuestionarioCompilato(questionarioCompilato);
}

    @Test
     void testGetCompilazioniInSospeso() {
        when(questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoFalse("test@example.com"))
            .thenReturn(Collections.singletonList(questionarioCompilato));

        List<QuestionarioCompilatoDTO> result = questionarioCompilatoService.getCompilazioniInSospeso("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getIdCompilazione());
        assertEquals("Titolo Questionario", result.get(0).getTitoloQuestionario());
        assertEquals("test@example.com", result.get(0).getEmailCreatore());

        verify(questionarioCompilatoRepository, times(1)).findByUtenteEmailAndDefinitivoFalse("test@example.com");
    }
    

    @Test
     void testCheckEmailUtenteIsNullForQuestionario() {
        when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));

        boolean result = questionarioCompilatoService.checkEmailUtenteIsNullForQuestionario(1);

        assertFalse(result);

        verify(questionarioCompilatoRepository, times(1)).findById(1);
    }

    @Test
     void testDeleteQuestionarioCompilatoAndRisposteByIdCompilazione() {
        when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));

        questionarioCompilatoService.deleteQuestionarioCompilatoAndRisposteByIdCompilazione(1);

        verify(rispostaRepository, times(1)).deleteByQuestionarioCompilato_IdCompilazione(1);
        verify(questionarioCompilatoRepository, times(1)).deleteByIdCompilazione(1);
    }

    @Test
 void testGetQuestionarioCompilatoById() {
    when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));
    when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(1))
        .thenReturn(Collections.singletonList(risposta));
    QuestionarioCompilatoDTO result = questionarioCompilatoService.getQuestionarioCompilatoById(1);
    assertNotNull(result);
    assertEquals(1, result.getIdCompilazione());
    assertEquals("Titolo Questionario", result.getTitoloQuestionario());
    assertEquals("test@example.com", result.getEmailCreatore());
    assertEquals(1, result.getRisposte().size());
    assertEquals("Risposta 1", result.getRisposte().get(0).getTestoRisposta());
    verify(questionarioCompilatoRepository, times(1)).findById(1);
    verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(1);
}

    @Test
     void testCheckIsDefinitivo() {
        when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));

        boolean result = questionarioCompilatoService.checkIsDefinitivo(1);

        assertFalse(result); 

        verify(questionarioCompilatoRepository, times(1)).findById(1);
    }

    @Test
 void testGetRisposteByCompilazione() {
    when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(1))
        .thenReturn(Collections.singletonList(risposta));

    List<RispostaDTO> result = questionarioCompilatoService.getRisposteByCompilazione(1);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getIdDomanda());
    assertEquals("Risposta 1", result.get(0).getTestoRisposta());
    verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(1);
}

    @Test
     void testGetDefinitiviByUtente() {
        questionarioCompilato.setDefinitivo(true);
        when(questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoTrue("test@example.com"))
            .thenReturn(Collections.singletonList(questionarioCompilato));
        when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(1))
            .thenReturn(Collections.singletonList(risposta));

        List<QuestionarioCompilatoDTO> result = questionarioCompilatoService.getDefinitiviByUtente("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getIdCompilazione());
        assertEquals("Titolo Questionario", result.get(0).getTitoloQuestionario());
        assertEquals(1, result.get(0).getRisposte().size());
        assertEquals("Risposta 1", result.get(0).getRisposte().get(0).getTestoRisposta());

        verify(questionarioCompilatoRepository, times(1)).findByUtenteEmailAndDefinitivoTrue("test@example.com");
        verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(1);
    }
}