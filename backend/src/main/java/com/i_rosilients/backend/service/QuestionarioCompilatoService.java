package com.i_rosilients.backend.service;

import java.util.ArrayList;
import java.util.List;
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

}
