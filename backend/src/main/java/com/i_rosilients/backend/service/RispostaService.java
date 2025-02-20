package com.i_rosilients.backend.service;


import com.i_rosilients.backend.model.Risposta;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Opzione;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.repository.RispostaRepository;
import com.i_rosilients.backend.repository.UtenteRepository;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.DomandaRepository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class RispostaService {

    @Autowired
    private RispostaRepository rispostaRepository;

    @Autowired
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired 
    private UtenteRepository utenteRepository;

    @Autowired
    private JavaMailSender emailSender;

    private static final String UPLOAD_DIR = "uploads/";

    // Per precompilare i campi
    public Map<Integer, String> getRisposteByIdCompilazione(int idCompilazione) {
        List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(idCompilazione);
        Map<Integer, String> Mapparisposte = new HashMap<>();
        
        for (Risposta risposta : risposte) {
            Mapparisposte.put(risposta.getDomanda().getIdDomanda(), risposta.getTestoRisposta());
        }
        
        return Mapparisposte;
    }

    // Crea una nuova compilazione
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
            // Se la risposta esiste, aggiorno il testo della risposta
            Risposta risposta = rispostaEsistente.get();
            risposta.setTestoRisposta(rispostaDTO.getTestoRisposta());
            rispostaRepository.save(risposta);  // Salvo la risposta aggiornata
        } else {
            // Se la risposta non esiste, ne creo una nuova
            Risposta nuovaRisposta = new Risposta();
            nuovaRisposta.setQuestionarioCompilato(questionarioCompilato);
            nuovaRisposta.setDomanda(domanda);
            nuovaRisposta.setTestoRisposta(rispostaDTO.getTestoRisposta());
            rispostaRepository.save(nuovaRisposta);  // Salvo la nuova risposta
        }    
    }

    public void finalizzaCompilazione(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione)
                .orElseThrow(() -> new RuntimeException("Compilazione non trovata"));

        questionarioCompilato.setDefinitivo(true); // Imposta lo stato del questionario a definitivo
        questionarioCompilatoRepository.save(questionarioCompilato);
    }

    public List<Risposta> getAllRisposteByIdCompilazione(int idCompilazione) {
        return rispostaRepository.findByQuestionarioCompilato_IdCompilazione(idCompilazione);
    }

    public void inviaEmailConPdf(String userEmail, int idCompilazione) {
        try {
            // Recupera il questionario compilato
            QuestionarioCompilato compilato = questionarioCompilatoRepository.findById(idCompilazione)
                    .orElseThrow(() -> new RuntimeException("Compilazione non trovata"));
    
            // Ottiene il titolo del questionario associato
            Questionario questionario = compilato.getQuestionario(); // Assumendo che ci sia una relazione con Questionario
            String titoloQuestionario = questionario.getNome(); // Ottieni il titolo del questionario
    
            // Ottiene tutte le risposte associate al questionario compilato
            List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato(compilato);
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
    
            // Aggiungi il titolo del questionario al PDF centrato
            Paragraph titolo = new Paragraph(titoloQuestionario, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            titolo.setAlignment(Element.ALIGN_CENTER);
            document.add(titolo);
            document.add(new Paragraph("\n"));
    
            // Definisci il font normale (non grassetto)
            Font fontNormale = FontFactory.getFont(FontFactory.HELVETICA, 12);
    
            for (Risposta risposta : risposte) {
                Domanda domanda = risposta.getDomanda();
    
                // Aggiungi la domanda con "Domanda:" in grassetto e il testo normale
                Paragraph domandaParagrafo = new Paragraph();
                domandaParagrafo.add(new Chunk("Domanda: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD))); // "Domanda:" in grassetto
                domandaParagrafo.add(new Chunk(domanda.getTestoDomanda(), fontNormale)); // Testo della domanda normale
                document.add(domandaParagrafo);
    
                // Se la domanda ha un'immagine, aggiungila al PDF
                if (domanda.getImmaginePath() != null && !domanda.getImmaginePath().isEmpty()) {
                    try {
                        // Converti il percorso relativo in percorso assoluto
                        String relativePath = domanda.getImmaginePath();
                        
                        // Il percorso assoluto potrebbe essere diverso in base alla struttura del tuo progetto
                        String absolutePath = UPLOAD_DIR + relativePath.replace("/api/domande/uploads/", "");
                        
                        // Carica l'immagine dal percorso assoluto
                        Image image = Image.getInstance(absolutePath);
    
                        // Ridimensiona l'immagine per adattarla al PDF
                        image.scaleToFit(400, 200); // Puoi cambiare queste dimensioni in base alle tue necessità
    
                        // Aggiungi l'immagine al PDF
                        document.add(image);
                    } catch (Exception e) {
                        System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
                    }
                }
    
                // Aggiungi la sezione "Opzioni" in grassetto
                if (!domanda.getOpzioni().isEmpty()) {
                    Paragraph opzioniParagrafo = new Paragraph();
                    opzioniParagrafo.add(new Chunk("Opzioni: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD))); // "Opzioni:" in grassetto
                    document.add(opzioniParagrafo);
    
                    // Aggiungi le opzioni, segnando la selezionata
                    for (Opzione opzione : domanda.getOpzioni()) {
                        Paragraph opzioneParagrafo = new Paragraph();
                        opzioneParagrafo.add(new Chunk("• " + opzione.getTestoOpzione(), fontNormale)); // Opzione normale
                        document.add(opzioneParagrafo);
                    }
                }
    
                // Aggiungi la risposta con "Risposta:" in grassetto e il testo normale
                Paragraph rispostaParagrafo = new Paragraph();
                rispostaParagrafo.add(new Chunk("Risposta: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD))); // "Risposta:" in grassetto
                rispostaParagrafo.add(new Chunk(risposta.getTestoRisposta(), fontNormale)); // Testo della risposta normale
                document.add(rispostaParagrafo);
    
                document.add(new Paragraph("\n")); // Aggiungi spazio tra le domande
            }
    
            document.close();
    
            // Invia l'email con il PDF allegato
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(userEmail);
            helper.setSubject("Questionario completato con successo!");
            helper.setText("Le tue risposte sono state correttamente salvate. In allegato trovi il PDF con le risposte del tuo questionario.");
    
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            helper.addAttachment("IlTuoQuestionario.pdf", resource);
    
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }    


}