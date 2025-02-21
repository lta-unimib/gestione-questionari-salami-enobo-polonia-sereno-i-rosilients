package com.i_rosilients.backend;

import com.i_rosilients.backend.controller.QuestionarioController;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.service.IQuestionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class QuestionarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IQuestionarioService questionarioService;

    @InjectMocks
    private QuestionarioController questionarioController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionarioController).build();
    }

    @Test
    public void testCreaQuestionario() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Test Questionario");
        questionarioDTO.setEmailUtente("test@example.com");

        doNothing().when(questionarioService).creaQuestionario(any(QuestionarioDTO.class));

        mockMvc.perform(post("/api/questionari/creaQuestionario")
                .contentType("application/json")
                .content("{\"nome\":\"Test Questionario\",\"emailUtente\":\"test@example.com\"}"))
                .andExpect(status().isOk());

        verify(questionarioService, times(1)).creaQuestionario(any(QuestionarioDTO.class));
    }

    @Test
    public void testCreaQuestionario_BadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid input")).when(questionarioService).creaQuestionario(any(QuestionarioDTO.class));

        mockMvc.perform(post("/api/questionari/creaQuestionario")
                .contentType("application/json")
                .content("{\"nome\":\"\",\"emailUtente\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(questionarioService, times(1)).creaQuestionario(any(QuestionarioDTO.class));
    }

    @Test
    public void testDeleteQuestionario() throws Exception {
        doNothing().when(questionarioService).deleteQuestionario(1);

        mockMvc.perform(delete("/api/questionari/deleteQuestionario/1"))
                .andExpect(status().isOk());

        verify(questionarioService, times(1)).deleteQuestionario(1);
    }

    @Test
    public void testDeleteQuestionario_NotFound() throws Exception {
        doThrow(new RuntimeException("Questionario non trovato")).when(questionarioService).deleteQuestionario(1);

        mockMvc.perform(delete("/api/questionari/deleteQuestionario/1"))
                .andExpect(status().isNotFound());

        verify(questionarioService, times(1)).deleteQuestionario(1);
    }

    @Test
    public void testUpdateQuestionario() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Updated Questionario");

        doNothing().when(questionarioService).updateQuestionario(1, questionarioDTO);

        mockMvc.perform(put("/api/questionari/updateQuestionario/1")
                .contentType("application/json")
                .content("{\"nome\":\"Updated Questionario\"}"))
                .andExpect(status().isOk());

        verify(questionarioService, times(1)).updateQuestionario(1, questionarioDTO);
    }

    @Test
    public void testUpdateQuestionario_NotFound() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Updated Questionario");

        doThrow(new RuntimeException("Questionario non trovato")).when(questionarioService).updateQuestionario(1, questionarioDTO);

        mockMvc.perform(put("/api/questionari/updateQuestionario/1")
                .contentType("application/json")
                .content("{\"nome\":\"Updated Questionario\"}"))
                .andExpect(status().isNotFound());

        verify(questionarioService, times(1)).updateQuestionario(1, questionarioDTO);
    }

    @Test
    public void testGetQuestionariByUtente() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Test Questionario");
        questionarioDTO.setEmailUtente("test@example.com");

        when(questionarioService.getQuestionariByUtente("test@example.com")).thenReturn(Collections.singletonList(questionarioDTO));

        mockMvc.perform(get("/api/questionari/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Test Questionario"))
                .andExpect(jsonPath("$[0].emailUtente").value("test@example.com"));

        verify(questionarioService, times(1)).getQuestionariByUtente("test@example.com");
    }

    @Test
    public void testSearchQuestionari() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Test Questionario");

        when(questionarioService.searchQuestionariWithQuestions("Test")).thenReturn(Collections.singletonList(questionarioDTO));

        mockMvc.perform(get("/api/questionari/search").param("nome", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Test Questionario"));

        verify(questionarioService, times(1)).searchQuestionariWithQuestions("Test");
    }

    @Test
    public void testGetTuttiIQuestionari() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Test Questionario");

        when(questionarioService.getTuttiIQuestionari()).thenReturn(Collections.singletonList(questionarioDTO));

        mockMvc.perform(get("/api/questionari/tuttiIQuestionari"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Test Questionario"));

        verify(questionarioService, times(1)).getTuttiIQuestionari();
    }

    @Test
    public void testGetDomandeByQuestionario() throws Exception {
        DomandaDTO domandaDTO = new DomandaDTO();
        domandaDTO.setTestoDomanda("Test Domanda");

        when(questionarioService.getDomandeByQuestionario(1)).thenReturn(Collections.singletonList(domandaDTO));

        mockMvc.perform(get("/api/questionari/1/domande"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].testoDomanda").value("Test Domanda"));

        verify(questionarioService, times(1)).getDomandeByQuestionario(1);
    }

    @Test
    public void testGetQuestionarioWithDomande() throws Exception {
        QuestionarioDTO questionarioDTO = new QuestionarioDTO();
        questionarioDTO.setNome("Test Questionario");

        when(questionarioService.getQuestionario(1)).thenReturn(questionarioDTO);

        mockMvc.perform(get("/api/questionari/1/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Test Questionario"));

        verify(questionarioService, times(1)).getQuestionario(1);
    }
}