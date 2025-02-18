package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.DomandaQuestionario;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.model.Risposta;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.RispostaRepository;
import com.i_rosilients.backend.repository.DomandaQuestionarioRepository;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.QuestionarioCompilatoRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionarioService implements IQuestionarioService {

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private RispostaRepository rispostaRepository;

    @Autowired
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

    @Autowired
    private QuestionarioCompilatoRepository questionarioCompilatoRepository;

    @Autowired
    private QuestionarioCompilatoService questionarioCompilatoService;

    public void creaQuestionario(QuestionarioDTO questionarioDTO) {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(questionarioDTO.getEmailUtente());

        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + questionarioDTO.getEmailUtente());
        }

        Questionario questionario = new Questionario(utenteOpt.get(), questionarioDTO.getNome());
        questionarioRepository.save(questionario);

        // Associa le domande al questionario
        if (questionarioDTO.getIdDomande() != null && !questionarioDTO.getIdDomande().isEmpty()) {
            for (Integer idDomanda : questionarioDTO.getIdDomande()) {
                if (domandaRepository.existsById(idDomanda)) {
                    DomandaQuestionario dq = new DomandaQuestionario();
                    dq.setIdDomanda(idDomanda);
                    dq.setIdQuestionario(questionario.getIdQuestionario());
                    domandaQuestionarioRepository.save(dq);
                }
            }
        }
    }

    

    @Transactional
    public void deleteQuestionario(int idQuestionario) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        if (questionarioOpt.isPresent()) {
            Questionario questionario = questionarioOpt.get();

            // Elimina i QuestionarioCompilato e le risposte associate
            questionarioCompilatoService.deleteQuestionarioCompilatoAndRisposte(questionario);

            // Rimuove tutte le associazioni domanda-questionario
            domandaQuestionarioRepository.deleteByQuestionario(questionario);

            // Elimina il questionario
            questionarioRepository.delete(questionario);
        } else {
            throw new RuntimeException("Questionario non trovato con id: " + idQuestionario);
        }
    }


    public void updateQuestionario(int idQuestionario, QuestionarioDTO questionarioDTO) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        if (questionarioOpt.isPresent()) {
            Questionario questionario = questionarioOpt.get();
            questionario.setNome(questionarioDTO.getNome());
            questionarioRepository.save(questionario);
    
            // Aggiorna le associazioni con DomandaQuestionario
            domandaQuestionarioRepository.deleteByQuestionario(questionario);
    
            if (questionarioDTO.getIdDomande() != null && !questionarioDTO.getIdDomande().isEmpty()) {
                for (Integer idDomanda : questionarioDTO.getIdDomande()) {
                    if (domandaRepository.existsById(idDomanda)) {
                        DomandaQuestionario dq = new DomandaQuestionario();
                        dq.setIdDomanda(idDomanda);
                        dq.setIdQuestionario(questionario.getIdQuestionario());
                        domandaQuestionarioRepository.save(dq);
                    }
                }
            }
        } else {
            throw new RuntimeException("Questionario non trovato con ID: " + idQuestionario);
        }
    }


    public List<QuestionarioDTO> getQuestionariByUtente(String emailUtente) {
        System.out.println("Ricevuta richiesta per email: " + emailUtente);

        Optional<Utente> utenteOpt = utenteRepository.findByEmail(emailUtente);
        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + emailUtente);
        }

        List<Questionario> questionari = questionarioRepository.findByUtente(utenteOpt.get());
        
        return questionari.stream()
            .map(questionario -> {
                // Recupera tutte le domande associate al questionario
                List<Integer> idDomande = domandaQuestionarioRepository.findByQuestionario(questionario)
                        .stream()
                        .map(dq -> dq.getDomanda().getIdDomanda())  // Estrai solo gli ID delle domande
                        .collect(Collectors.toList());

                return new QuestionarioDTO(
                    questionario.getIdQuestionario(),
                    questionario.getNome(),
                    questionario.getUtente().getEmail(),
                    idDomande  // Aggiunge gli ID delle domande al DTO
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<QuestionarioDTO> searchQuestionariWithQuestions(String nome) {
        List<QuestionarioDTO> questionari = questionarioRepository.searchQuestionariWithQuestions(nome);
        
        for (QuestionarioDTO questionario : questionari) {
            List<Integer> domandeIds = domandaRepository.findDomandeIdsByQuestionarioId(questionario.getIdQuestionario());
            questionario.setIdDomande(domandeIds);
        }
    
        return questionari;
    }

    @Override
    public List<DomandaDTO> getDomandeByQuestionario(int idQuestionario) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        if (questionarioOpt.isEmpty()) {
            throw new RuntimeException("Questionario non trovato con ID: " + idQuestionario);
        }

        List<DomandaDTO> domande = domandaQuestionarioRepository.findByQuestionario(questionarioOpt.get())
                .stream()
                .map(dq -> {
                    Domanda domanda = dq.getDomanda();
                    return new DomandaDTO(
                        domanda.getIdDomanda(),
                        domanda.getArgomento(),
                        domanda.getTestoDomanda(),
                        domanda.getUtente().getEmail(),
                        domanda.getImmaginePath(),
                        false,
                        domanda.getOpzioni().stream().map(opzione -> opzione.getTestoOpzione()).collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
    

        return domande;
    }

    @Override
    public QuestionarioDTO getQuestionario(int idQuestionario) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        if (questionarioOpt.isEmpty()) {
            throw new RuntimeException("Questionario non trovato con ID: " + idQuestionario);
        }

        Questionario questionario = questionarioOpt.get();
        return new QuestionarioDTO(
            questionario.getIdQuestionario(),
            questionario.getNome(),
            questionario.getUtente().getEmail()
        );
    }

}