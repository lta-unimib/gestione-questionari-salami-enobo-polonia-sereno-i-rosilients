package com.i_rosilients.backend;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.i_rosilients.backend.controller.RispostaController;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.risposta.IGestoreRisposta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
 class RispostaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IGestoreRisposta rispostaService;

    @InjectMocks
    private RispostaController rispostaController;

    @BeforeEach
     void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(rispostaController).build();
    }

    @Test
     void testCreaCompilazione_Success() throws Exception {
        when(rispostaService.creaNuovaCompilazione(1, "test@example.com")).thenReturn(123);
        mockMvc.perform(post("/api/risposte/creaCompilazione")
                .param("idQuestionario", "1")
                .param("userEmail", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompilazione").value(123))
                .andExpect(jsonPath("$.message").value("Compilazione creata con successo"));
        verify(rispostaService, times(1)).creaNuovaCompilazione(1, "test@example.com");
    }

    @Test
     void testCreaCompilazione_Failure() throws Exception {
        when(rispostaService.creaNuovaCompilazione(1, "test@example.com"))
                .thenThrow(new RuntimeException("Errore durante la creazione della compilazione"));
        mockMvc.perform(post("/api/risposte/creaCompilazione")
                .param("idQuestionario", "1")
                .param("userEmail", "test@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Errore durante la creazione della compilazione"));
        verify(rispostaService, times(1)).creaNuovaCompilazione(1, "test@example.com");
    }

    @Test
     void testGetRisposteByIdCompilazione() throws Exception {
        Map<Integer, String> risposte = new HashMap<>();
        risposte.put(1, "Risposta 1");
        when(rispostaService.getRisposteByIdCompilazione(1)).thenReturn(risposte);
        mockMvc.perform(get("/api/risposte/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1").value("Risposta 1"));
        verify(rispostaService, times(1)).getRisposteByIdCompilazione(1);
    }

    @Test
     void testSalvaRisposta_Success() throws Exception {
        RispostaDTO rispostaDTO = new RispostaDTO();
        rispostaDTO.setIdCompilazione(1);
        rispostaDTO.setIdDomanda(1);
        rispostaDTO.setTestoRisposta("Risposta 1");
        mockMvc.perform(post("/api/risposte/salvaRisposta")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idCompilazione\":1,\"idDomanda\":1,\"testoRisposta\":\"Risposta 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Risposta salvata con successo"));
        verify(rispostaService, times(1)).salvaRisposta(any(RispostaDTO.class));
    }

    @Test
     void testSalvaRisposta_Failure() throws Exception {
        doThrow(new RuntimeException("Errore durante il salvataggio della risposta"))
                .when(rispostaService).salvaRisposta(any(RispostaDTO.class));
        mockMvc.perform(post("/api/risposte/salvaRisposta")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idCompilazione\":1,\"idDomanda\":1,\"testoRisposta\":\"Risposta 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Errore durante il salvataggio della risposta"));
        verify(rispostaService, times(1)).salvaRisposta(any(RispostaDTO.class));
    }

    @Test
     void testFinalizzaCompilazione_Success() throws Exception {
        mockMvc.perform(post("/api/risposte/finalizzaCompilazione")
                .param("idCompilazione", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Compilazione finalizzata con successo"));
        verify(rispostaService, times(1)).finalizzaCompilazione(1);
    }

    @Test
     void testFinalizzaCompilazione_Failure() throws Exception {
        doThrow(new RuntimeException("Errore durante la finalizzazione della compilazione"))
                .when(rispostaService).finalizzaCompilazione(1);
        mockMvc.perform(post("/api/risposte/finalizzaCompilazione")
                .param("idCompilazione", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Errore durante la finalizzazione della compilazione"));
        verify(rispostaService, times(1)).finalizzaCompilazione(1);
    }

    @Test
     void testInviaEmail_Success() throws Exception {
        mockMvc.perform(post("/api/risposte/inviaEmail")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userEmail\":\"test@example.com\",\"idCompilazione\":1}"))
                .andExpect(status().isOk());
        verify(rispostaService, times(1)).inviaEmailConPdf("test@example.com", 1);
    }

    @Test
     void testInviaEmail_Failure() throws Exception {
        doThrow(new RuntimeException("Errore durante l'invio dell'email"))
                .when(rispostaService).inviaEmailConPdf("test@example.com", 1);
        mockMvc.perform(post("/api/risposte/inviaEmail")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userEmail\":\"test@example.com\",\"idCompilazione\":1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Errore durante l'invio dell'email"));
        verify(rispostaService, times(1)).inviaEmailConPdf("test@example.com", 1);
    }
}