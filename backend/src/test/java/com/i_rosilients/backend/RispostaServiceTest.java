package com.i_rosilients.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.Opzione;
import com.i_rosilients.backend.model.questionario.Questionario;
import com.i_rosilients.backend.model.questionarioCompilato.QuestionarioCompilato;
import com.i_rosilients.backend.model.risposta.GestoreRisposta;
import com.i_rosilients.backend.model.risposta.Risposta;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.*;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
 class RispostaServiceTest {

    @Mock
    private RispostaRepository rispostaRepository;

    @Mock
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Mock
    private DomandaRepository domandaRepository;

    @Mock
    private QuestionarioRepository questionarioRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private GestoreRisposta rispostaService;

    private QuestionarioCompilato questionarioCompilato;
    private Questionario questionario;
    private Utente utente;
    private Domanda domanda;
    private Risposta risposta;

    @BeforeEach
     void setup() {
        utente = new Utente();
        utente.setEmail("test@example.com");

        questionario = new Questionario();
        questionario.setIdQuestionario(1);
        questionario.setNome("Titolo Questionario");
        questionario.setUtente(utente);

        domanda = new Domanda();
        domanda.setIdDomanda(1);
        domanda.setTestoDomanda("Domanda 1");
        domanda.setImmaginePath("/path/to/image");
        domanda.setUtente(utente);

        Opzione opzione1 = new Opzione();
        opzione1.setIdOpzione(1);
        opzione1.setTestoOpzione("Opzione 1");
        opzione1.setDomanda(domanda);

        Opzione opzione2 = new Opzione();
        opzione2.setIdOpzione(2);
        opzione2.setTestoOpzione("Opzione 2");
        opzione2.setDomanda(domanda);
        domanda.setOpzioni(Arrays.asList(opzione1, opzione2));

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
     void testGetRisposteByIdCompilazione() {
        when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(1))
            .thenReturn(Collections.singletonList(risposta));
        Map<Integer, String> result = rispostaService.getRisposteByIdCompilazione(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Risposta 1", result.get(1));
        verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(1);
    }

    @Test
 void testCreaNuovaCompilazione() {
    when(questionarioRepository.findById(1)).thenReturn(Optional.of(questionario));
    when(utenteRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utente));
    when(questionarioCompilatoRepository.save(any(QuestionarioCompilato.class)))
        .thenReturn(questionarioCompilato);
    int result = rispostaService.creaNuovaCompilazione(1, "test@example.com");
    assertEquals(1, result);
    verify(questionarioRepository, times(1)).findById(1);
    verify(utenteRepository, times(1)).findByEmail("test@example.com");
    verify(questionarioCompilatoRepository, times(1)).save(any(QuestionarioCompilato.class));
}

    @Test
     void testSalvaRisposta_NuovaRisposta() {
        when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));
        when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));
        when(rispostaRepository.findByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(1, 1))
            .thenReturn(Optional.empty());
        RispostaDTO rispostaDTO = new RispostaDTO();
        rispostaDTO.setIdCompilazione(1);
        rispostaDTO.setIdDomanda(1);
        rispostaDTO.setTestoRisposta("Risposta 1");
        rispostaService.salvaRisposta(rispostaDTO);
        verify(rispostaRepository, times(1)).save(any(Risposta.class));
    }

    @Test
     void testSalvaRisposta_RispostaEsistente() {
        when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));
        when(domandaRepository.findById(1)).thenReturn(Optional.of(domanda));
        when(rispostaRepository.findByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(1, 1))
            .thenReturn(Optional.of(risposta));
        RispostaDTO rispostaDTO = new RispostaDTO();
        rispostaDTO.setIdCompilazione(1);
        rispostaDTO.setIdDomanda(1);
        rispostaDTO.setTestoRisposta("Risposta Aggiornata");
        rispostaService.salvaRisposta(rispostaDTO);
        verify(rispostaRepository, times(1)).save(risposta);
    }

    @Test
     void testFinalizzaCompilazione() {
        when(questionarioCompilatoRepository.findById(1)).thenReturn(Optional.of(questionarioCompilato));
        rispostaService.finalizzaCompilazione(1);
        verify(questionarioCompilatoRepository, times(1)).save(questionarioCompilato);
        assertTrue(questionarioCompilato.isDefinitivo());
    }

    @Test
     void testGetAllRisposteByIdCompilazione() {
        when(rispostaRepository.findByQuestionarioCompilato_IdCompilazione(1))
            .thenReturn(Collections.singletonList(risposta));
        List<Risposta> result = rispostaService.getAllRisposteByIdCompilazione(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Risposta 1", result.get(0).getTestoRisposta());
        verify(rispostaRepository, times(1)).findByQuestionarioCompilato_IdCompilazione(1);
    }

    @Test
void testInviaEmailConPdf() {
    int idCompilazione = 1;
    String userEmail = "test@example.com";
    ReflectionTestUtils.setField(rispostaService, "supportEmail", "support@example.com");
    when(questionarioCompilatoRepository.findById(idCompilazione))
        .thenReturn(Optional.of(questionarioCompilato));
    when(rispostaRepository.findByQuestionarioCompilato(questionarioCompilato))
        .thenReturn(Collections.singletonList(risposta));
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    rispostaService.inviaEmailConPdf(userEmail, idCompilazione);
    verify(questionarioCompilatoRepository, times(1)).findById(idCompilazione);
    verify(rispostaRepository, times(1)).findByQuestionarioCompilato(questionarioCompilato);
    verify(emailSender, times(1)).createMimeMessage();
    verify(emailSender, times(1)).send(mimeMessage);

    assertFalse(domanda.getOpzioni().isEmpty()); 
    assertEquals(2, domanda.getOpzioni().size()); 
}
}
