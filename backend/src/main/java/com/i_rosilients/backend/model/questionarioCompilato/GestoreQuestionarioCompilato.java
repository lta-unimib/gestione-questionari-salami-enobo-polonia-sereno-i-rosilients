package com.i_rosilients.backend.model.questionarioCompilato;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.risposta.Risposta;
import com.i_rosilients.backend.services.persistence.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.services.persistence.RispostaRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class GestoreQuestionarioCompilato implements IGestoreQuestionarioCompilato{

    private final QuestionarioCompilatoRepository questionarioCompilatoRepository;
    private final RispostaRepository rispostaRepository;
    private final JavaMailSender emailSender;

    public GestoreQuestionarioCompilato(
        QuestionarioCompilatoRepository questionarioCompilatoRepository, 
        RispostaRepository rispostaRepository,
        JavaMailSender emailSender){
        this.questionarioCompilatoRepository = questionarioCompilatoRepository;
        this.rispostaRepository = rispostaRepository;
        this.emailSender = emailSender;
    }

    public List<QuestionarioCompilatoDTO> getCompilazioniInSospeso(String email) {
        List<QuestionarioCompilato> compilazioni = questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoFalse(email);
    
        return compilazioni.stream().map(compilazione -> {
            List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(compilazione.getIdCompilazione());
    
            List<RispostaDTO> risposteDTOs = risposte.stream()
                .map(risposta -> new RispostaDTO(
                    risposta.getQuestionarioCompilato().getIdCompilazione(),
                    risposta.getDomanda().getIdDomanda(),
                    risposta.getTestoRisposta()
                ))
                .collect(Collectors.toList());
    
            return new QuestionarioCompilatoDTO(
                compilazione.getIdCompilazione(),
                compilazione.getQuestionario().getIdQuestionario(),
                compilazione.getQuestionario().getNome(),
                compilazione.getQuestionario().getUtente().getEmail(),
                compilazione.getDataCompilazione(),
                risposteDTOs
            );
        }).collect(Collectors.toList());
    }

    public boolean checkEmailUtenteIsNullForQuestionario(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione)
            .orElse(null);
        if (questionarioCompilato == null) {
            return false;
        }
        return (questionarioCompilato.getUtente() == null);
    }

    @Transactional
    public void deleteQuestionarioCompilatoAndRisposteByIdCompilazione(int idCompilazione) {
        Optional<QuestionarioCompilato> questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione);
        if (questionarioCompilato.isEmpty()) {
            System.out.println("❌ Nessun questionario compilato trovato per ID: " + idCompilazione);
            return;
        }
        rispostaRepository.deleteByQuestionarioCompilato_IdCompilazione(idCompilazione);
        questionarioCompilatoRepository.deleteByIdCompilazione(idCompilazione);
    }
      

    public QuestionarioCompilatoDTO getQuestionarioCompilatoById(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione)
            .orElse(null);
    
        if (questionarioCompilato == null) {
            System.out.println(" Nessun questionario compilato trovato per ID: " + idCompilazione);
            return null;
        }
    
        List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(idCompilazione);
        System.out.println("✅ Numero risposte trovate: " + risposte.size());
    
        return new QuestionarioCompilatoDTO(
            questionarioCompilato.getIdCompilazione(),
            questionarioCompilato.getQuestionario().getIdQuestionario(),
            questionarioCompilato.getQuestionario().getNome(),
            getEmailUtenteByCompilazione(questionarioCompilato),
            questionarioCompilato.getDataCompilazione(),
            risposte.stream()
                .map(r -> new RispostaDTO(r.getDomanda().getIdDomanda(), r.getTestoRisposta()))
                .collect(Collectors.toList())
        );
    }

    public List<QuestionarioCompilatoDTO> getQuestionariCompilatiByUtenteAndIdQuestionario(String userEmail, int idQuestionario) {
        List<QuestionarioCompilato> compilazioni = questionarioCompilatoRepository
        .findByUtenteEmailNotOrNullAndQuestionarioIdQuestionarioAndDefinitivo(userEmail, idQuestionario);
    
        if (compilazioni == null) {
            System.out.println(" Nessun questionario compilato trovato per email:" + userEmail + " ID: " + idQuestionario);
            return null;
        }

        return compilazioni.stream().map(compilazione -> {

            return new QuestionarioCompilatoDTO(
                compilazione.getIdCompilazione(),
                compilazione.getQuestionario().getIdQuestionario(),
                compilazione.getQuestionario().getNome(),
                getEmailUtenteByCompilazione(compilazione),
                compilazione.getDataCompilazione(),
                new ArrayList<>()
            );
        }).collect(Collectors.toList());
    }

    public String getEmailUtenteByCompilazione(QuestionarioCompilato questionarioCompilato) {
        
        if (questionarioCompilato.getUtente() != null) {
            return questionarioCompilato.getUtente().getEmail();
        }
        return "Anonymous";
    }

    public boolean checkIsDefinitivo(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findByIdCompilazione(idCompilazione)
            .orElse(null);
    
        if (questionarioCompilato == null) {
            System.out.println("❌ Nessun questionario compilato trovato per ID: " + idCompilazione);
            return false;
        }
    
        return questionarioCompilato.isDefinitivo();
    }

    public List<RispostaDTO> getRisposteByCompilazione(int idCompilazione) {
        List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(idCompilazione);
    
        return risposte.stream()
            .map(r -> new RispostaDTO(r.getDomanda().getIdDomanda(), r.getTestoRisposta()))
            .collect(Collectors.toList());
    }

    public List<QuestionarioCompilatoDTO> getDefinitiviByUtente(String email) {
        
        List<QuestionarioCompilato> compilazioniDefinitive = questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoTrue(email);
        
        return compilazioniDefinitive.stream().map(compilazione -> {
            
            List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(compilazione.getIdCompilazione());
            
            List<RispostaDTO> risposteDTOs = risposte.stream()
                .map(risposta -> new RispostaDTO(
                    risposta.getQuestionarioCompilato().getIdCompilazione(),
                    risposta.getDomanda().getIdDomanda(),
                    risposta.getTestoRisposta()
                ))
                .collect(Collectors.toList());
             
            return new QuestionarioCompilatoDTO(
                compilazione.getIdCompilazione(),
                compilazione.getQuestionario().getIdQuestionario(),
                compilazione.getQuestionario().getNome(),
                compilazione.getQuestionario().getUtente().getEmail(),
                compilazione.getDataCompilazione(),
                risposteDTOs 
            );
        }).collect(Collectors.toList());
    }

    public List<QuestionarioCompilatoDTO> getAllByUtente(String userEmail) {
        
        List<QuestionarioCompilato> compilazioni = questionarioCompilatoRepository.findByUtenteEmail(userEmail);
        
        return compilazioni.stream().map(compilazione -> {
            
            List<Risposta> risposte = rispostaRepository.findByQuestionarioCompilato_IdCompilazione(compilazione.getIdCompilazione());
            
            List<RispostaDTO> risposteDTOs = risposte.stream()
                .map(risposta -> new RispostaDTO(
                    risposta.getQuestionarioCompilato().getIdCompilazione(),
                    risposta.getDomanda().getIdDomanda(),
                    risposta.getTestoRisposta()
                ))
                .collect(Collectors.toList());
             
            return new QuestionarioCompilatoDTO(
                compilazione.getIdCompilazione(),
                compilazione.getQuestionario().getIdQuestionario(),
                compilazione.getQuestionario().getNome(),
                compilazione.getQuestionario().getUtente().getEmail(),
                compilazione.getDataCompilazione(),
                risposteDTOs 
            );
        }).collect(Collectors.toList());
    }

    public void inviaEmail(int idCompilazione, String userEmail) {
        try {
            QuestionarioCompilato compilato = questionarioCompilatoRepository.findByIdCompilazione(idCompilazione)
                    .orElse(null);
            if (compilato == null) {
                throw new RuntimeException("Compilazione non trovata");
            }
    
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(userEmail);
            helper.setSubject("Cancellazione Compilazione!!!");
            helper.setText("La tua compilazione con ID: " + idCompilazione + " per il questionario: " + compilato.getQuestionario().getNome() + " è stata cancellata dal suo proprietario.");
    
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }

}
