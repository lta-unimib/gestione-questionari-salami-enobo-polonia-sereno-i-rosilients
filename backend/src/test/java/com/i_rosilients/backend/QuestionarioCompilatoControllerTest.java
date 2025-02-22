package com.i_rosilients.backend;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.i_rosilients.backend.controller.QuestionarioCompilatoController;
import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.service.IQuestionarioCompilatoService;

@ExtendWith(MockitoExtension.class)
 class QuestionarioCompilatoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IQuestionarioCompilatoService questionarioCompilatoService;

    @InjectMocks
    private QuestionarioCompilatoController questionarioCompilatoController;

    @BeforeEach
     void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionarioCompilatoController).build();
    }

    @Test
     void testDeleteQuestionarioCompilatoAndRisposteByIdCompilazione() throws Exception {
        int idCompilazione = 1;
        doNothing().when(questionarioCompilatoService).deleteQuestionarioCompilatoAndRisposteByIdCompilazione(idCompilazione);

        mockMvc.perform(delete("/api/questionariCompilati/deleteQuestionarioCompilato/{idCompilazione}", idCompilazione))
                .andExpect(status().isOk())
                .andExpect(content().string("Questionario compilato eliminato con successo"));

        verify(questionarioCompilatoService, times(1)).deleteQuestionarioCompilatoAndRisposteByIdCompilazione(idCompilazione);
    }

    @Test
     void testGetQuestionarioCompilato() throws Exception {
        int idCompilazione = 1;
        QuestionarioCompilatoDTO questionarioCompilatoDTO = new QuestionarioCompilatoDTO(
                1, "Titolo Questionario", "creatore@example.com", LocalDateTime.now(), Collections.emptyList()
        );
        when(questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione)).thenReturn(questionarioCompilatoDTO);

        mockMvc.perform(get("/api/questionariCompilati/{idCompilazione}", idCompilazione))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idQuestionario").value(1))
                .andExpect(jsonPath("$.titoloQuestionario").value("Titolo Questionario"))
                .andExpect(jsonPath("$.emailCreatore").value("creatore@example.com"));

        verify(questionarioCompilatoService, times(1)).getQuestionarioCompilatoById(idCompilazione);
    }

    @Test
     void testGetQuestionarioCompilatoNonRegistrato() throws Exception {
        int idCompilazione = 1;
        QuestionarioCompilatoDTO questionarioCompilatoDTO = new QuestionarioCompilatoDTO(
                1, "Titolo Questionario", null, LocalDateTime.now(), Collections.emptyList()
        );
        when(questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione)).thenReturn(questionarioCompilatoDTO);
        when(questionarioCompilatoService.checkEmailUtenteIsNullForQuestionario(idCompilazione)).thenReturn(true);

        mockMvc.perform(get("/api/questionariCompilati/utenteNonRegistrato/{idCompilazione}", idCompilazione))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailCreatore").doesNotExist());

        verify(questionarioCompilatoService, times(1)).getQuestionarioCompilatoById(idCompilazione);
        verify(questionarioCompilatoService, times(1)).checkEmailUtenteIsNullForQuestionario(idCompilazione);
    }

    @Test
     void testCheckIsDefinitivo() throws Exception {
        int idCompilazione = 1;
        when(questionarioCompilatoService.checkIsDefinitivo(idCompilazione)).thenReturn(true);

        mockMvc.perform(get("/api/questionariCompilati/checkIsDefinitivo/{idCompilazione}", idCompilazione))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(questionarioCompilatoService, times(1)).checkIsDefinitivo(idCompilazione);
    }

    @Test
     void testGetQuestionariCompilatiInSospesoUtente() throws Exception {
        String userEmail = "test@example.com";
        List<QuestionarioCompilatoDTO> questionari = Arrays.asList(
                new QuestionarioCompilatoDTO(1, "Titolo 1", userEmail, LocalDateTime.now(), Collections.emptyList()),
                new QuestionarioCompilatoDTO(2, "Titolo 2", userEmail, LocalDateTime.now(), Collections.emptyList())
        );
        when(questionarioCompilatoService.getCompilazioniInSospeso(userEmail)).thenReturn(questionari);

        mockMvc.perform(get("/api/questionariCompilati/inSospeso/utente/{userEmail}", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titoloQuestionario").value("Titolo 1"))
                .andExpect(jsonPath("$[1].titoloQuestionario").value("Titolo 2"));

        verify(questionarioCompilatoService, times(1)).getCompilazioniInSospeso(userEmail);
    }

    @Test
 void testGetRisposteByCompilazione() throws Exception {
    int idCompilazione = 1;
    List<RispostaDTO> risposte = Arrays.asList(
            new RispostaDTO(idCompilazione, 1, "Risposta 1"), // idCompilazione, idDomanda, testoRisposta
            new RispostaDTO(idCompilazione, 2, "Risposta 2")
    );
    when(questionarioCompilatoService.getRisposteByCompilazione(idCompilazione)).thenReturn(risposte);

    mockMvc.perform(get("/api/questionariCompilati/{idCompilazione}/risposte", idCompilazione))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].idCompilazione").value(idCompilazione)) // Verifica idCompilazione
            .andExpect(jsonPath("$[0].idDomanda").value(1)) // Verifica idDomanda
            .andExpect(jsonPath("$[0].testoRisposta").value("Risposta 1")) // Verifica testoRisposta
            .andExpect(jsonPath("$[1].idCompilazione").value(idCompilazione))
            .andExpect(jsonPath("$[1].idDomanda").value(2))
            .andExpect(jsonPath("$[1].testoRisposta").value("Risposta 2"));

    verify(questionarioCompilatoService, times(1)).getRisposteByCompilazione(idCompilazione);
}

    @Test
     void testGetDefinitiviByUtente() throws Exception {
        String userEmail = "test@example.com";
        List<QuestionarioCompilatoDTO> questionariDefinitivi = Arrays.asList(
                new QuestionarioCompilatoDTO(1, "Titolo 1", userEmail, LocalDateTime.now(), Collections.emptyList()),
                new QuestionarioCompilatoDTO(2, "Titolo 2", userEmail, LocalDateTime.now(), Collections.emptyList())
        );
        when(questionarioCompilatoService.getDefinitiviByUtente(userEmail)).thenReturn(questionariDefinitivi);

        mockMvc.perform(get("/api/questionariCompilati/definitivi/utente/{userEmail}", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titoloQuestionario").value("Titolo 1"))
                .andExpect(jsonPath("$[1].titoloQuestionario").value("Titolo 2"));

        verify(questionarioCompilatoService, times(1)).getDefinitiviByUtente(userEmail);
    }

    @Test
     void testGetDefinitiviByUtente_NoContent() throws Exception {
        String userEmail = "test@example.com";
        when(questionarioCompilatoService.getDefinitiviByUtente(userEmail)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/questionariCompilati/definitivi/utente/{userEmail}", userEmail))
                .andExpect(status().isNoContent());

        verify(questionarioCompilatoService, times(1)).getDefinitiviByUtente(userEmail);
    }
}
