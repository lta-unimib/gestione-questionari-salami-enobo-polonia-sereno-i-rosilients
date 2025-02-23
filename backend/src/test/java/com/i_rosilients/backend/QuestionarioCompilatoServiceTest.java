package com.i_rosilients.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
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

import jakarta.mail.internet.MimeMessage;

 class QuestionarioCompilatoServiceTest {

    @Mock
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Mock
    private RispostaRepository rispostaRepository;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private QuestionarioCompilatoService questionarioCompilatoService;

    private QuestionarioCompilato questionarioCompilato;
    private Questionario questionario;
    private Utente utente;
    private Risposta risposta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    String email = "test@example.com";
    when(questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoFalse(email))
        .thenReturn(Collections.singletonList(questionarioCompilato));

    when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione()))
        .thenReturn(Collections.singletonList(risposta));

    List<QuestionarioCompilatoDTO> result = questionarioCompilatoService.getCompilazioniInSospeso(email);
    assertNotNull(result);
    assertEquals(1, result.size());
    QuestionarioCompilatoDTO dto = result.get(0);
    assertEquals(questionarioCompilato.getIdCompilazione(), dto.getIdCompilazione());
    assertEquals(questionario.getIdQuestionario(), dto.getIdQuestionario());
    assertEquals(questionario.getNome(), dto.getTitoloQuestionario());
    assertEquals(questionario.getUtente().getEmail(), dto.getEmailCreatore());
    assertEquals(questionarioCompilato.getDataCompilazione(), dto.getDataCompilazione());
    List<RispostaDTO> risposteDTOs = dto.getRisposte();
    assertNotNull(risposteDTOs);
    assertEquals(1, risposteDTOs.size());
    RispostaDTO rispostaDTO = risposteDTOs.get(0);
    assertEquals(risposta.getQuestionarioCompilato().getIdCompilazione(), rispostaDTO.getIdCompilazione());
    assertEquals(risposta.getDomanda().getIdDomanda(), rispostaDTO.getIdDomanda());
    assertEquals(risposta.getTestoRisposta(), rispostaDTO.getTestoRisposta());
    verify(questionarioCompilatoRepository, times(1)).findByUtenteEmailAndDefinitivoFalse(email);
    verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());
    }

    @Test
    void testCheckEmailUtenteIsNullForQuestionario() {
        int idCompilazione = 1;
        QuestionarioCompilato compilazione = new QuestionarioCompilato();
        compilazione.setUtente(null);
        when(questionarioCompilatoRepository.findById(idCompilazione))
            .thenReturn(Optional.of(compilazione));
        boolean result = questionarioCompilatoService.checkEmailUtenteIsNullForQuestionario(idCompilazione);
        assertTrue(result);
        verify(questionarioCompilatoRepository, times(1)).findById(idCompilazione);
    }

    @Test
    void testDeleteQuestionarioCompilatoAndRisposteByIdCompilazione() {
        int idCompilazione = 1;
        QuestionarioCompilato compilazione = new QuestionarioCompilato();
        compilazione.setIdCompilazione(idCompilazione);

        when(questionarioCompilatoRepository.findById(idCompilazione))
            .thenReturn(Optional.of(compilazione));
        questionarioCompilatoService.deleteQuestionarioCompilatoAndRisposteByIdCompilazione(idCompilazione);
        verify(rispostaRepository, times(1)).deleteByQuestionarioCompilato_IdCompilazione(idCompilazione);
        verify(questionarioCompilatoRepository, times(1)).deleteByIdCompilazione(idCompilazione);
    }
    @Test
    void testDeleteQuestionarioCompilatoAndRisposte() {
        Questionario questionario = new Questionario();
        questionario.setIdQuestionario(1);
        QuestionarioCompilato compilazione = new QuestionarioCompilato();
        compilazione.setIdCompilazione(1);
        when(questionarioCompilatoRepository.findByQuestionario(questionario))
            .thenReturn(Arrays.asList(compilazione));
        questionarioCompilatoService.deleteQuestionarioCompilatoAndRisposte(questionario);
        verify(rispostaRepository, times(1)).deleteByQuestionarioCompilato_IdCompilazione(1);
        verify(questionarioCompilatoRepository, times(1)).deleteByQuestionario(questionario);
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
    QuestionarioCompilato questionario = new QuestionarioCompilato();
    questionario.setDefinitivo(true);
    when(questionarioCompilatoRepository.findByIdCompilazione(1))
        .thenReturn(Optional.of(questionario));

    boolean result = questionarioCompilatoService.checkIsDefinitivo(1);
    assertTrue(result);
    verify(questionarioCompilatoRepository, times(1)).findByIdCompilazione(1);
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

    @Test
    void testGetQuestionariCompilatiByUtenteAndIdQuestionario() {
        String email = "test@example.com";
        int idQuestionario = 1;
        when(questionarioCompilatoRepository.findByUtenteEmailNotOrNullAndQuestionarioIdQuestionarioAndDefinitivo(email, idQuestionario))
            .thenReturn(Arrays.asList(questionarioCompilato));
        List<QuestionarioCompilatoDTO> result = questionarioCompilatoService.getQuestionariCompilatiByUtenteAndIdQuestionario(email, idQuestionario);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(questionarioCompilato.getIdCompilazione(), result.get(0).getIdCompilazione());
        assertEquals(questionario.getNome(), result.get(0).getTitoloQuestionario());
        verify(questionarioCompilatoRepository, times(1))
            .findByUtenteEmailNotOrNullAndQuestionarioIdQuestionarioAndDefinitivo(email, idQuestionario);
    }

    @Test
    void testGetAllByUtente() {
        String email = "test@example.com";
        when(questionarioCompilatoRepository.findByUtenteEmail(email))
            .thenReturn(Arrays.asList(questionarioCompilato));
        when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione()))
            .thenReturn(Arrays.asList(risposta));
        List<QuestionarioCompilatoDTO> result = questionarioCompilatoService.getAllByUtente(email);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(questionarioCompilato.getIdCompilazione(), result.get(0).getIdCompilazione());
        assertEquals(questionario.getNome(), result.get(0).getTitoloQuestionario());
        assertEquals(1, result.get(0).getRisposte().size());
        assertEquals(risposta.getTestoRisposta(), result.get(0).getRisposte().get(0).getTestoRisposta());
        verify(questionarioCompilatoRepository, times(1)).findByUtenteEmail(email);
        verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());
    }

    @Test
    void testInviaEmail() {
        int idCompilazione = 1;
        String email = "test@example.com";
        when(questionarioCompilatoRepository.findByIdCompilazione(idCompilazione))
            .thenReturn(Optional.of(questionarioCompilato));
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        questionarioCompilatoService.inviaEmail(idCompilazione, email);
        verify(questionarioCompilatoRepository, times(1)).findByIdCompilazione(idCompilazione);
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }
}