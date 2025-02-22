package com.i_rosilients.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.model.Risposta;
import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.RispostaRepository;
import jakarta.transaction.Transactional;

@Service
public class QuestionarioCompilatoService implements IQuestionarioCompilatoService{

    private final QuestionarioCompilatoRepository questionarioCompilatoRepository;
    private final RispostaRepository rispostaRepository;

    public QuestionarioCompilatoService(QuestionarioCompilatoRepository questionarioCompilatoRepository, RispostaRepository rispostaRepository){
        this.questionarioCompilatoRepository = questionarioCompilatoRepository;
        this.rispostaRepository = rispostaRepository;
    }

    public List<QuestionarioCompilatoDTO> getCompilazioniInSospeso(String email) {
        List<QuestionarioCompilato> compilazioni = questionarioCompilatoRepository.findByUtenteEmailAndDefinitivoFalse(email);
    
        return compilazioni.stream().map(compilazione -> {
            return new QuestionarioCompilatoDTO(
                compilazione.getIdCompilazione(),
                compilazione.getQuestionario().getIdQuestionario(),
                compilazione.getQuestionario().getNome(),
                compilazione.getQuestionario().getUtente().getEmail(),
                compilazione.getDataCompilazione(),
                new ArrayList<>()
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

    @Transactional
    public void deleteQuestionarioCompilatoAndRisposte(Questionario questionario) {
        List<QuestionarioCompilato> questionariCompilati =  questionarioCompilatoRepository.findByQuestionario(questionario);

        for (QuestionarioCompilato questionarioCompilato : questionariCompilati) {
            rispostaRepository.deleteByQuestionarioCompilato_IdCompilazione(questionarioCompilato.getIdCompilazione());
        }
        questionarioCompilatoRepository.deleteByQuestionario(questionario);
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
            questionarioCompilato.getQuestionario().getUtente().getEmail(),
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
                compilazione.getQuestionario().getUtente().getEmail(),
                compilazione.getDataCompilazione(),
                new ArrayList<>()
            );
        }).collect(Collectors.toList());
    }

    public boolean checkIsDefinitivo(int idCompilazione) {
        QuestionarioCompilato questionarioCompilato = questionarioCompilatoRepository.findById(idCompilazione)
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

}
