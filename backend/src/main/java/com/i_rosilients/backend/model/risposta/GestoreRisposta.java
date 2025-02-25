package com.i_rosilients.backend.model.risposta;


import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.Opzione;
import com.i_rosilients.backend.model.questionario.Questionario;
import com.i_rosilients.backend.model.questionarioCompilato.QuestionarioCompilato;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.services.persistence.QuestionarioRepository;
import com.i_rosilients.backend.services.persistence.RispostaRepository;
import com.i_rosilients.backend.services.persistence.UtenteRepository;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import jakarta.mail.MessagingException;


@Service
public class GestoreRisposta implements IGestoreRisposta {
    private final RispostaRepository rispostaRepository;
    private final QuestionarioCompilatoRepository questionarioCompilatoRepository;
    private final DomandaRepository domandaRepository;
    private final QuestionarioRepository questionarioRepository;
    private final UtenteRepository utenteRepository;
    private final JavaMailSender emailSender;
    @Value("${support.email}") 
    private String supportEmail;
    private static final String UPLOAD_DIR = "uploads/";

    public GestoreRisposta(
            RispostaRepository rispostaRepository,
            QuestionarioCompilatoRepository questionarioCompilatoRepository,
            DomandaRepository domandaRepository,
            QuestionarioRepository questionarioRepository,
            UtenteRepository utenteRepository,
            JavaMailSender emailSender) {
        this.rispostaRepository = rispostaRepository;
        this.questionarioCompilatoRepository = questionarioCompilatoRepository;
        this.domandaRepository = domandaRepository;
        this.questionarioRepository = questionarioRepository;
        this.utenteRepository = utenteRepository;
        this.emailSender = emailSender;
    }

    
    @Override
    public Map<Integer, String> getRisposteByIdCompilazione(int idCompilazione) {
        List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(idCompilazione);
        Map<Integer, String> Mapparisposte = new HashMap<>();
        
        for (Risposta risposta : risposte) {
            Mapparisposte.put(risposta.getDomanda().getIdDomanda(), risposta.getTestoRisposta());
        }
        
        return Mapparisposte;
    }

    // Crea una nuova compilazione
    @Override
    public int creaNuovaCompilazione(int idQuestionario, String userEmail) {
        Questionario questionario = questionarioRepository.findById(idQuestionario)
                .orElseThrow(() -> new RuntimeException("Questionario non trovato"));

        QuestionarioCompilato nuovaCompilazione = new QuestionarioCompilato();
        nuovaCompilazione.setQuestionario(questionario);
        nuovaCompilazione.setDataCompilazione(LocalDateTime.now());
        if(userEmail.equals("")) {
            nuovaCompilazione.setUtente(null);            
        }
        else {
            Optional<Utente> utenteOpt = utenteRepository.findByEmail(userEmail);
            if (utenteOpt.isEmpty()) {
                throw new RuntimeException("Utente non trovato con email: " + userEmail);
            }
            nuovaCompilazione.setUtente(utenteOpt.get());
        }    
        nuovaCompilazione.setDefinitivo(false);

        QuestionarioCompilato compilazioneSalvata = questionarioCompilatoRepository.save(nuovaCompilazione);
        return compilazioneSalvata.getIdCompilazione();
    }

    // Salva una risposta
    @Override
    public void salvaRisposta(RispostaDTO rispostaDTO) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(rispostaDTO.getIdCompilazione())
                .orElseThrow(() -> new RuntimeException("QuestionarioCompilato non trovato"));

        if (questionarioCompilato.isDefinitivo()) {
            throw new RuntimeException("Il Questionario è definitivo e non può essere modificato");
        }

        Domanda domanda = domandaRepository.findById(rispostaDTO.getIdDomanda())
                .orElseThrow(() -> new RuntimeException("Domanda non trovata"));

        Optional<Risposta> rispostaEsistente = rispostaRepository.findByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(
            rispostaDTO.getIdCompilazione(),
            rispostaDTO.getIdDomanda()
        );

        if (rispostaEsistente.isPresent()) {
            Risposta risposta = rispostaEsistente.get();
            risposta.setTestoRisposta(rispostaDTO.getTestoRisposta());
            rispostaRepository.save(risposta);
        } else {
            Risposta nuovaRisposta = new Risposta();
            nuovaRisposta.setQuestionarioCompilato(questionarioCompilato);
            nuovaRisposta.setDomanda(domanda);
            nuovaRisposta.setTestoRisposta(rispostaDTO.getTestoRisposta());
            rispostaRepository.save(nuovaRisposta);
        }    
    }

    @Override
    public void finalizzaCompilazione(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione)
                .orElseThrow(() -> new RuntimeException("Compilazione non trovata"));

        questionarioCompilato.setDefinitivo(true);
        questionarioCompilatoRepository.save(questionarioCompilato);
    }

    @Override
    public void inviaEmailConPdf(String userEmail, int idCompilazione) {
        try {
            QuestionarioCompilato compilato = questionarioCompilatoRepository.findById(idCompilazione)
                    .orElseThrow(() -> new RuntimeException("Compilazione non trovata"));
    
            Questionario questionario = compilato.getQuestionario();
            String titoloQuestionario = questionario.getNome();
    
            List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato(compilato);
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
    
            Paragraph titolo = new Paragraph(titoloQuestionario, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            titolo.setAlignment(Element.ALIGN_CENTER);
            document.add(titolo);
            document.add(new Paragraph("\n"));
    
            Font fontNormale = FontFactory.getFont(FontFactory.HELVETICA, 12);
    
            for (Risposta risposta : risposte) {
                Domanda domanda = risposta.getDomanda();
    
                Paragraph domandaParagrafo = new Paragraph();
                domandaParagrafo.add(new Chunk("Domanda: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                domandaParagrafo.add(new Chunk(domanda.getTestoDomanda(), fontNormale));
                document.add(domandaParagrafo);
    
                if (domanda.getImmaginePath() != null && !domanda.getImmaginePath().isEmpty()) {
                    try {
                        String relativePath = domanda.getImmaginePath();
                        String absolutePath = UPLOAD_DIR + relativePath.replace("/api/domande/uploads/", "");
                        Image image = Image.getInstance(absolutePath);
                        image.scaleToFit(400, 200);
                        document.add(image);
                    } catch (DocumentException | IOException e) {
                        System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
                    }
                }
    
                if (!domanda.getOpzioni().isEmpty()) {
                    Paragraph opzioniParagrafo = new Paragraph();
                    opzioniParagrafo.add(new Chunk("Opzioni: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    document.add(opzioniParagrafo);
    
                    for (Opzione opzione : domanda.getOpzioni()) {
                        Paragraph opzioneParagrafo = new Paragraph();
                        opzioneParagrafo.add(new Chunk("• " + opzione.getTestoOpzione(), fontNormale));
                        document.add(opzioneParagrafo);
                    }
                }
    
                Paragraph rispostaParagrafo = new Paragraph();
                rispostaParagrafo.add(new Chunk("Risposta: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                rispostaParagrafo.add(new Chunk(risposta.getTestoRisposta(), fontNormale));
                document.add(rispostaParagrafo);
    
                document.add(new Paragraph("\n"));
            }
    
            document.close();
    
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(userEmail);
            helper.setFrom(supportEmail);
            helper.setSubject("Questionario completato con successo!");
            helper.setText("Le tue risposte sono state correttamente salvate. In allegato trovi il PDF con le risposte del tuo questionario.");
    
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            helper.addAttachment("IlTuoQuestionario.pdf", resource);
    
            emailSender.send(message);
        } catch (DocumentException | MessagingException | MailException e) {
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }
}