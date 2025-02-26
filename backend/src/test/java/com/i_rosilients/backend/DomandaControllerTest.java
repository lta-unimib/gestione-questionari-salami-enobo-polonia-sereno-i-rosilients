package com.i_rosilients.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i_rosilients.backend.controller.DomandaController;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.IGestoreDomanda;
import com.i_rosilients.backend.services.persistence.DomandaRepository;

import io.jsonwebtoken.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
 class DomandaControllerTest {
/*
    private MockMvc mockMvc;

    @Mock
    private IGestoreDomanda domandaService;

    @Mock
    private DomandaRepository domandaRepository;

    @InjectMocks
    private DomandaController domandaController;

    private ObjectMapper objectMapper;

    @BeforeEach
     void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(domandaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
     void testCreaDomanda() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", "image/png", "test image".getBytes());
        List<String> opzioni = Arrays.asList("Opzione 1", "Opzione 2");
        String opzioniJson = objectMapper.writeValueAsString(opzioni);

        doNothing().when(domandaService).creaDomanda(any(DomandaDTO.class));

        mockMvc.perform(multipart("/api/domande/creaDomanda")
                .file(imageFile)
                .param("argomento", "Test Argomento")
                .param("testoDomanda", "Test Domanda")
                .param("emailUtente", "test@example.com")
                .param("opzioni", opzioniJson))
                .andExpect(status().isOk());

        verify(domandaService, times(1)).creaDomanda(any(DomandaDTO.class));
    }

    @Test
     void testCreaDomanda_BadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid input")).when(domandaService).creaDomanda(any(DomandaDTO.class));

        mockMvc.perform(multipart("/api/domande/creaDomanda")
                .param("argomento", "")
                .param("testoDomanda", "")
                .param("emailUtente", ""))
                .andExpect(status().isBadRequest());

        verify(domandaService, times(1)).creaDomanda(any(DomandaDTO.class));
    }

    @Test
        void testUpdateDomanda() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", "image/png", "test image".getBytes());
        List<String> opzioni = Arrays.asList("Opzione 1", "Opzione 2");
        String opzioniJson = objectMapper.writeValueAsString(opzioni);

        Domanda domanda = new Domanda();
        domanda.setIdDomanda(1);
        when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));

        doNothing().when(domandaService).updateDomanda(anyInt(), any(DomandaDTO.class));


        mockMvc.perform(MockMvcRequestBuilders.put("/api/domande/updateDomanda/1")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("argomento", "Updated Argomento")
                .param("testoDomanda", "Updated Domanda")
                .param("emailUtente", "test@example.com")
                .param("opzioni", opzioniJson)
                .param("removeImage", "false")
                .content(imageFile.getBytes()))
                .andExpect(status().isOk());

        verify(domandaService, times(1)).updateDomanda(anyInt(), any(DomandaDTO.class));
        }

    @Test
        void testUpdateDomanda_RemoveImage() throws Exception {

        Domanda domanda = new Domanda();
        domanda.setIdDomanda(1);
        domanda.setImmaginePath("uploads/test.png"); // Simula un'immagine esistente
        when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));

        doNothing().when(domandaService).updateDomanda(anyInt(), any(DomandaDTO.class));


        mockMvc.perform(MockMvcRequestBuilders.put("/api/domande/updateDomanda/1")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("argomento", "Updated Argomento")
                .param("testoDomanda", "Updated Domanda")
                .param("emailUtente", "test@example.com")
                .param("removeImage", "true"))
                .andExpect(status().isOk());


        verify(domandaService, times(1)).updateDomanda(anyInt(), any(DomandaDTO.class));
}

@Test
void testUpdateDomanda_UploadNewImage() throws Exception {

    Domanda domanda = new Domanda();
    domanda.setIdDomanda(1);
    when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));

    doNothing().when(domandaService).updateDomanda(anyInt(), any(DomandaDTO.class));


    MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", "image/png", "test image".getBytes());
    mockMvc.perform(MockMvcRequestBuilders.put("/api/domande/updateDomanda/1")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("argomento", "Updated Argomento")
            .param("testoDomanda", "Updated Domanda")
            .param("emailUtente", "test@example.com")
            .param("removeImage", "false")
            .content(imageFile.getBytes()))
            .andExpect(status().isOk());


    verify(domandaService, times(1)).updateDomanda(anyInt(), any(DomandaDTO.class));
        }

    @Test
void testUpdateDomanda_NotFound() throws Exception {
    when(domandaRepository.findById(1)).thenReturn(Optional.empty());

    mockMvc.perform(MockMvcRequestBuilders.put("/api/domande/updateDomanda/1")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("argomento", "Updated Argomento")
            .param("testoDomanda", "Updated Domanda")
            .param("emailUtente", "test@example.com")
            .param("removeImage", "false"))
            .andExpect(status().isNotFound());

    verify(domandaService, never()).updateDomanda(anyInt(), any(DomandaDTO.class));
}

@Test
void testUpdateDomanda_InvalidOpzioniFormat() throws Exception {

    Domanda domanda = new Domanda();
    domanda.setIdDomanda(1);
    when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));


    mockMvc.perform(MockMvcRequestBuilders.put("/api/domande/updateDomanda/1")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("argomento", "Updated Argomento")
            .param("testoDomanda", "Updated Domanda")
            .param("emailUtente", "test@example.com")
            .param("opzioni", "invalid json"))
            .andExpect(status().isBadRequest());


    verify(domandaService, never()).updateDomanda(anyInt(), any(DomandaDTO.class));
}

@Test
void testUpdateDomanda_ImageSaveError() throws Exception {

    Domanda domanda = new Domanda();
    domanda.setIdDomanda(1);
    when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));


    MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", "image/png", "test image".getBytes());
    doThrow(new IOException("Errore durante il salvataggio dell'immagine")).when(domandaService).updateDomanda(anyInt(), any(DomandaDTO.class));

    mockMvc.perform(MockMvcRequestBuilders.put("/api/domande/updateDomanda/1")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("argomento", "Updated Argomento")
            .param("testoDomanda", "Updated Domanda")
            .param("emailUtente", "test@example.com")
            .param("removeImage", "false")
            .content(imageFile.getBytes()))
            .andExpect(status().isInternalServerError());


    verify(domandaService, times(1)).updateDomanda(anyInt(), any(DomandaDTO.class));
}
    @Test
     void testDeleteDomanda() throws Exception {
        doNothing().when(domandaService).deleteDomanda(1);

        mockMvc.perform(delete("/api/domande/deleteDomanda/1"))
                .andExpect(status().isOk());

        verify(domandaService, times(1)).deleteDomanda(1);
    }

    @Test
     void testDeleteDomanda_NotFound() throws Exception {
        doThrow(new RuntimeException("Domanda non trovata")).when(domandaService).deleteDomanda(1);

        mockMvc.perform(delete("/api/domande/deleteDomanda/1"))
                .andExpect(status().isNotFound());

        verify(domandaService, times(1)).deleteDomanda(1);
    }

    @Test
     void testGetDomandeByUtente() throws Exception {
        DomandaDTO domandaDTO = new DomandaDTO();
        domandaDTO.setTestoDomanda("Test Domanda");

        when(domandaService.getDomandeByUtente("test@example.com")).thenReturn(Collections.singletonList(domandaDTO));

        mockMvc.perform(get("/api/domande/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].testoDomanda").value("Test Domanda"));

        verify(domandaService, times(1)).getDomandeByUtente("test@example.com");
    }

    @Test
     void testGetTutteLeDomande() throws Exception {
        DomandaDTO domandaDTO = new DomandaDTO();
        domandaDTO.setTestoDomanda("Test Domanda");

        when(domandaService.getTutteLeDomande()).thenReturn(Collections.singletonList(domandaDTO));

        mockMvc.perform(get("/api/domande/tutteLeDomande"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].testoDomanda").value("Test Domanda"));

        verify(domandaService, times(1)).getTutteLeDomande();
    }

    @Test
     void testGetDomandeByQuestionario() throws Exception {
        DomandaDTO domandaDTO = new DomandaDTO();
        domandaDTO.setTestoDomanda("Test Domanda");

        when(domandaService.getDomandeByQuestionario("1")).thenReturn(Collections.singletonList(domandaDTO));

        mockMvc.perform(get("/api/domande/domandeByQuestionario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].testoDomanda").value("Test Domanda"));

        verify(domandaService, times(1)).getDomandeByQuestionario("1");
    }

    @Test
     void testGetImage() throws Exception {
        Path imagePath = Paths.get("uploads/test.png");
        Files.createDirectories(imagePath.getParent());
        Files.createFile(imagePath);

        mockMvc.perform(get("/api/domande/uploads/test.png"))
                .andExpect(status().isOk());

        Files.deleteIfExists(imagePath);
    }

    @Test
     void testGetImage_NotFound() throws Exception {
        mockMvc.perform(get("/api/domande/uploads/nonexistent.png"))
                .andExpect(status().isNotFound());
    }
 
    @Test
void testDetermineContentType_Png() {
    DomandaController controller = new DomandaController(domandaService, domandaRepository);
    String contentType = controller.determineContentType("test.png");
    assertEquals("image/png", contentType);
}

@Test
void testDetermineContentType_Jpg() {
    DomandaController controller = new DomandaController(domandaService, domandaRepository);
    String contentType = controller.determineContentType("test.jpg");
    assertEquals("image/jpeg", contentType);
}

@Test
void testDetermineContentType_Jpeg() {
    DomandaController controller = new DomandaController(domandaService, domandaRepository);
    String contentType = controller.determineContentType("test.jpeg");
    assertEquals("image/jpeg", contentType);
}

@Test
void testDetermineContentType_Gif() {
    DomandaController controller = new DomandaController(domandaService, domandaRepository);
    String contentType = controller.determineContentType("test.gif");
    assertEquals("image/gif", contentType);
}

@Test
void testDetermineContentType_Unsupported() {
    DomandaController controller = new DomandaController(domandaService, domandaRepository);
    String contentType = controller.determineContentType("test.xyz");
    assertEquals("application/octet-stream", contentType);
}
    */
}
