package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

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

    public void creaQuestionario(QuestionarioDTO questionarioDTO) {

        Optional<Utente> utenteOpt = 
        utenteRepository.findByEmail(questionarioDTO.getEmailUtente()); // controlla che esista l'utente

        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + questionarioDTO.getEmailUtente());
        }

        Questionario questionario = new Questionario(utenteOpt.get(), questionarioDTO.getNome());

        questionarioRepository.save(questionario);     
    }
    
    public void deleteQuestionario(int idQuestionario) {
        Optional<Questionario> questionarioOpt = questionarioRepository.findById(idQuestionario);
        if (questionarioOpt.isPresent()) {
            questionarioRepository.delete(questionarioOpt.get());
        } else {
            throw new RuntimeException("Questionario non trovato con id: " + idQuestionario);
        }
    }


    public List<QuestionarioDTO> getQuestionariByUtente(String emailUtente) {
        System.out.println("Ricevuta richiesta per email: " + emailUtente);

        Optional<Utente> utenteOpt = utenteRepository.findByEmail(emailUtente);
        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + emailUtente);
        }

        List<Questionario> questionari = questionarioRepository.findByUtente(utenteOpt.get());
        System.out.println("Questionari trovati: " + questionari.size());  // Aggiungi questo log
        return questionari.stream()
                .map(questionario -> new QuestionarioDTO(
                    questionario.getIdQuestionario(),
                    questionario.getNome(),
                    questionario.getUtente().getEmail()
                ))
                .collect(Collectors.toList());
    }
}