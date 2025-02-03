package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.QuestionarioRepository;
import com.i_rosilients.backend.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionarioService {/*
    private final QuestionarioRepository questionarioRepository;
    private final UtenteRepository utenteRepository;

    public QuestionarioService(QuestionarioRepository questionarioRepository, UtenteRepository utenteRepository) {
        this.questionarioRepository = questionarioRepository;
        this.utenteRepository = utenteRepository;
    }

    public Questionario creaQuestionario(QuestionarioDTO questionarioDTO) {
        Optional<Utente> utenteOpt = utenteRepository.findById(questionarioDTO.getEmailUtente());

        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + questionarioDTO.getEmailUtente());
        }

        Questionario questionario = new Questionario();
        questionario.setNome(questionarioDTO.getNome());
        questionario.setUtente(utenteOpt.get());

        return questionarioRepository.save(questionario);
    }

    public List<QuestionarioDTO> getQuestionariByUtente(String emailUtente) {
        Optional<Utente> utenteOpt = utenteRepository.findById(emailUtente);

        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + emailUtente);
        }

        return questionarioRepository.findByUtente(utenteOpt.get()).stream()
                .map(q -> {
                    QuestionarioDTO dto = new QuestionarioDTO();
                    dto.setIdQuestionario(q.getIdQuestionario());
                    dto.setNome(q.getNome());
                    dto.setEmailUtente(q.getUtente().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }*/
}
