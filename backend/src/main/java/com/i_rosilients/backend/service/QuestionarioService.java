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
public class QuestionarioService {

    @Autowired
    private QuestionarioRepository questionarioRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    public void creaQuestionario(QuestionarioDTO questionarioDTO) {

        Optional<Utente> utenteOpt = 
        utenteRepository.findById(questionarioDTO.getEmailUtente()); // controlla che esista l'utente

        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + questionarioDTO.getEmailUtente());
        }

        Questionario questionario = new Questionario(utenteOpt.get(), questionarioDTO.getNome());

        questionarioRepository.save(questionario);     
    }

    public List<QuestionarioDTO> getQuestionariByUtente(String emailUtente) {
        Optional<Utente> utenteOpt = utenteRepository.findById(emailUtente);
        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + emailUtente);
        }

        return questionarioRepository.findByUtente(utenteOpt.get()).stream()
                .map(questionario -> {

                    QuestionarioDTO dto = new QuestionarioDTO(
                        questionario.getIdQuestionario(),
                        questionario.getNome(),
                        questionario.getUtente().getEmail()
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }
}