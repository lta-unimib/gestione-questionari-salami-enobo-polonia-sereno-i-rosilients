package com.i_rosilients.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.i_rosilients.backend.controller.DomandaController;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.domanda.IGestoreDomanda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DomandaControllerTest {

    @Mock
    private IGestoreDomanda domandaService;

    @InjectMocks
    private DomandaController domandaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void creaDomanda_Success() throws IOException, JsonProcessingException {
                MultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        when(domandaService.parseOpzioniJson(anyString())).thenReturn(Arrays.asList("opzione1", "opzione2"));
        when(domandaService.salvaImmagine(any(MultipartFile.class))).thenReturn("/uploads/test.png");

                ResponseEntity<String> response = domandaController.creaDomanda(
                "argomento", "testoDomanda", "email@test.com", "[\"opzione1\", \"opzione2\"]", imageFile);

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Domanda creata con successo", response.getBody());
        verify(domandaService, times(1)).creaDomanda(any(DomandaDTO.class));
    }

    @Test
    void creaDomanda_IOException() throws IOException, JsonProcessingException {
                MultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        when(domandaService.parseOpzioniJson(anyString())).thenReturn(Arrays.asList("opzione1", "opzione2"));
        when(domandaService.salvaImmagine(any(MultipartFile.class))).thenThrow(new IOException("Errore di I/O"));

                ResponseEntity<String> response = domandaController.creaDomanda(
                "argomento", "testoDomanda", "email@test.com", "[\"opzione1\", \"opzione2\"]", imageFile);

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Errore nel salvataggio dell'immagine", response.getBody());
    }

    @Test
    void updateDomanda_Success() throws IOException, JsonProcessingException {
                MultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        when(domandaService.parseOpzioniJson(anyString())).thenReturn(Arrays.asList("opzione1", "opzione2"));
        when(domandaService.salvaImmagine(any(MultipartFile.class))).thenReturn("/uploads/test.png");

                ResponseEntity<String> response = domandaController.updateDomanda(
                1, "argomento", "testoDomanda", "email@test.com", "[\"opzione1\", \"opzione2\"]", imageFile, false);

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Domanda aggiornata con successo", response.getBody());
        verify(domandaService, times(1)).updateDomanda(anyInt(), any(DomandaDTO.class));
    }

    @Test
    void deleteDomanda_Success() {
                doNothing().when(domandaService).deleteDomanda(anyInt());

                ResponseEntity<String> response = domandaController.deleteDomanda(1);

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Domanda eliminata con successo", response.getBody());
        verify(domandaService, times(1)).deleteDomanda(anyInt());
    }

    @Test
    void deleteDomanda_NotFound() {
                doThrow(new RuntimeException("Domanda non trovata")).when(domandaService).deleteDomanda(anyInt());

                ResponseEntity<String> response = domandaController.deleteDomanda(1);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Domanda non trovata", response.getBody());
    }

    @Test
    void getDomandeByUtente_Success() {
                DomandaDTO domandaDTO = new DomandaDTO("argomento", "testoDomanda", "email@test.com", null, Arrays.asList("opzione1", "opzione2"));
        when(domandaService.getDomandeByUtente(anyString())).thenReturn(Arrays.asList(domandaDTO));

                ResponseEntity<List<DomandaDTO>> response = domandaController.getDomandeByUtente("email@test.com");

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("argomento", response.getBody().get(0).getArgomento());
    }

    @Test
    void getTutteLeDomande_Success() {
                DomandaDTO domandaDTO = new DomandaDTO("argomento", "testoDomanda", "email@test.com", null, Arrays.asList("opzione1", "opzione2"));
        when(domandaService.getTutteLeDomande()).thenReturn(Arrays.asList(domandaDTO));

                ResponseEntity<List<DomandaDTO>> response = domandaController.getTutteLeDomande();

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("argomento", response.getBody().get(0).getArgomento());
    }

    @Test
    void getDomandeByQuestionario_Success() {
                DomandaDTO domandaDTO = new DomandaDTO("argomento", "testoDomanda", "email@test.com", null, Arrays.asList("opzione1", "opzione2"));
        when(domandaService.getDomandeByQuestionario(anyString())).thenReturn(Arrays.asList(domandaDTO));

                ResponseEntity<List<DomandaDTO>> response = domandaController.getDomandeByQuestionario("1");

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("argomento", response.getBody().get(0).getArgomento());
    }

    @Test
    void getImage_Success() throws IOException {
                Resource resource = mock(Resource.class);
        when(domandaService.getImage(anyString())).thenReturn(ResponseEntity.ok(resource));

                ResponseEntity<Resource> response = domandaController.getImage("test.png");

                assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resource, response.getBody());
    }
}