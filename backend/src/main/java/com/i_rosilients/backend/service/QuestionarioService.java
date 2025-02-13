package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.DomandaQuestionario;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.DomandaQuestionarioRepository;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
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
    private DomandaQuestionarioRepository domandaQuestionarioRepository;

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
    
    public void deleteQuestionario(int idQuestionario) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        if (questionarioOpt.isPresent()) {
            // Rimuove tutte le associazioni domanda-questionario
            domandaQuestionarioRepository.deleteByQuestionario(questionarioOpt.get());
            
            // Elimina il questionario
            questionarioRepository.delete(questionarioOpt.get());
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
    
    public List<Questionario> searchQuestionariWithQuestions(String nome) {
        return questionarioRepository.findQuestionariWithQuestions(nome);
    }
}