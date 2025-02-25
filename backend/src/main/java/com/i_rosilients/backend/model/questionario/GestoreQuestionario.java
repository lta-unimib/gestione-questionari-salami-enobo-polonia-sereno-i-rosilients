package com.i_rosilients.backend.model.questionario;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.DomandaQuestionarioRepository;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.QuestionarioRepository;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GestoreQuestionario implements IGestoreQuestionario {

    private final QuestionarioRepository questionarioRepository;
    private final UtenteRepository utenteRepository;
    private final DomandaRepository domandaRepository;
    private final DomandaQuestionarioRepository domandaQuestionarioRepository;

    public GestoreQuestionario(QuestionarioRepository questionarioRepository, UtenteRepository utenteRepository, DomandaRepository domandaRepository, DomandaQuestionarioRepository domandaQuestionarioRepository){
        this.questionarioRepository = questionarioRepository;
        this.utenteRepository = utenteRepository;
        this.domandaRepository = domandaRepository;
        this.domandaQuestionarioRepository = domandaQuestionarioRepository;
    }

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

            // Elimina il questionario
            questionarioRepository.delete(questionario);
        } else {
            throw new RuntimeException("Questionario non trovato con id: " + idQuestionario);
        }
    }

    @Transactional
    public void updateQuestionario(int idQuestionario, QuestionarioDTO questionarioDTO) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        
        if (questionarioOpt.isEmpty()) {
            throw new RuntimeException("Questionario non trovato con ID: " + idQuestionario);
        }

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
    public List<QuestionarioDTO> getTuttiIQuestionari() {
        List<Questionario> questionari = questionarioRepository.findAll();  // Recupera tutti i questionari dal DB
        
        // Crea la lista di QuestionarioDTO
        return questionari.stream()
            .map(questionario -> {
    
                // Recupera gli ID delle domande associate al questionario
                List<Integer> domandeIds = domandaRepository.findDomandeIdsByQuestionarioId(questionario.getIdQuestionario());
    
                // Creazione del QuestionarioDTO
                return new QuestionarioDTO(
                    questionario.getIdQuestionario(),
                    questionario.getNome(),
                    questionario.getUtente().getEmail(),  // L'utente che ha creato il questionario
                    domandeIds  // Aggiungi solo gli ID delle domande
                );
            })
            .collect(Collectors.toList());
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
    
        // Estrai gli ID delle domande tramite la relazione domandeQuestionario
        List<Integer> idDomande = questionario.getDomandeQuestionario().stream()
            .map(domandaQuestionario -> domandaQuestionario.getDomanda().getIdDomanda()) // Ottieni l'ID della domanda
            .collect(Collectors.toList());
    
        // Restituisci il DTO popolato con i dettagli del questionario e gli ID delle domande
        return new QuestionarioDTO(
            questionario.getIdQuestionario(),
            questionario.getNome(),
            questionario.getUtente().getEmail(),
            idDomande  // Lista di ID domande
        );
    }

}