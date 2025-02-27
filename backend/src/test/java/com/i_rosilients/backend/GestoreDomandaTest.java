package com.i_rosilients.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.GestoreDomanda;
import com.i_rosilients.backend.model.domanda.Opzione;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.DomandaQuestionarioRepository;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.OpzioneRepository;
import com.i_rosilients.backend.services.persistence.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestoreDomandaTest {

    @Mock
    private DomandaRepository domandaRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private OpzioneRepository opzioneRepository;

    @Mock
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

    @InjectMocks
    private GestoreDomanda gestoreDomanda;

    private Domanda domanda;
    private Utente utente;
    private DomandaDTO domandaDTO;

    @BeforeEach
    void setUp() {
        utente = new Utente();
        utente.setEmail("test@example.com");

        domanda = new Domanda();
        domanda.setIdDomanda(1);
        domanda.setArgomento("Argomento di test");
        domanda.setTestoDomanda("Testo di test");
        domanda.setImmaginePath("/uploads/test.png");
        domanda.setUtente(utente);

        domandaDTO = new DomandaDTO();
        domandaDTO.setArgomento("Argomento di test");
        domandaDTO.setTestoDomanda("Testo di test");
        domandaDTO.setEmailUtente("test@example.com");
        domandaDTO.setImagePath("/uploads/test.png");
        domandaDTO.setOpzioni(List.of("Opzione 1", "Opzione 2"));
    }

    @Test
    void creaDomanda_Success() throws IOException, JsonProcessingException {
                when(utenteRepository.findByEmail(anyString())).thenReturn(Optional.of(utente));
        when(domandaRepository.save(any(Domanda.class))).thenReturn(domanda);

                gestoreDomanda.creaDomanda(domandaDTO);

                verify(utenteRepository, times(1)).findByEmail(anyString());
        verify(domandaRepository, times(1)).save(any(Domanda.class));
        verify(opzioneRepository, times(2)).save(any(Opzione.class));
    }

    @Test
    void creaDomanda_UtenteNonTrovato() {
                when(utenteRepository.findByEmail(anyString())).thenReturn(Optional.empty());

                RuntimeException exception = assertThrows(RuntimeException.class, () -> gestoreDomanda.creaDomanda(domandaDTO));
        assertEquals("Utente non trovato con email: " + domandaDTO.getEmailUtente(), exception.getMessage());
    }

    @Test
    void updateDomanda_Success() throws IOException {
                when(domandaRepository.findById(anyInt())).thenReturn(Optional.of(domanda));
        when(domandaRepository.save(any(Domanda.class))).thenReturn(domanda);

                gestoreDomanda.updateDomanda(1, domandaDTO);

                verify(domandaRepository, times(1)).findById(anyInt());
        verify(domandaRepository, times(1)).save(any(Domanda.class));
        verify(opzioneRepository, times(2)).save(any(Opzione.class));
    }

    @Test
    void updateDomanda_RimozioneImmagine() throws IOException {
                domandaDTO.setRemoveImage(true);
        when(domandaRepository.findById(anyInt())).thenReturn(Optional.of(domanda));
        when(domandaRepository.save(any(Domanda.class))).thenReturn(domanda);

                gestoreDomanda.updateDomanda(1, domandaDTO);

                verify(domandaRepository, times(1)).findById(anyInt());
        verify(domandaRepository, times(1)).save(any(Domanda.class));
        assertNull(domanda.getImmaginePath());
    }

    @Test
    void updateDomanda_SostituzioneImmagine() throws IOException {
                domandaDTO.setImagePath("/uploads/new_image.png");
        when(domandaRepository.findById(anyInt())).thenReturn(Optional.of(domanda));
        when(domandaRepository.save(any(Domanda.class))).thenReturn(domanda);

                gestoreDomanda.updateDomanda(1, domandaDTO);

                verify(domandaRepository, times(1)).findById(anyInt());
        verify(domandaRepository, times(1)).save(any(Domanda.class));
        assertEquals("/uploads/new_image.png", domanda.getImmaginePath());
    }

    @Test
    void salvaImmagine_FileNull() {
                MultipartFile file = null;

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gestoreDomanda.salvaImmagine(file));
        assertEquals("File cannot be null", exception.getMessage());
    }

    @Test
    void eliminaImmagine_Success() throws IOException {
                Path path = Paths.get("uploads/test.png");
        Files.createDirectories(path.getParent());
        Files.createFile(path);

                gestoreDomanda.eliminaImmagine("/uploads/test.png");

                assertFalse(Files.exists(path));
    }

    @Test
    void deleteDomanda_Success() throws IOException {
                when(domandaRepository.findById(anyInt())).thenReturn(Optional.of(domanda));

                gestoreDomanda.deleteDomanda(1);

                verify(domandaRepository, times(1)).findById(anyInt());
        verify(domandaRepository, times(1)).delete(any(Domanda.class));
    }

    @Test
    void deleteDomanda_ImmagineNonEliminata() throws IOException {
                domanda.setImmaginePath(null);
        when(domandaRepository.findById(anyInt())).thenReturn(Optional.of(domanda));

                gestoreDomanda.deleteDomanda(1);

                verify(domandaRepository, times(1)).findById(anyInt());
        verify(domandaRepository, times(1)).delete(any(Domanda.class));
    }
}