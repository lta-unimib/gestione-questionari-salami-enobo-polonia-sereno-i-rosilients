package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DomandaService implements IDomandaService {

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    public void creaDomanda(DomandaDTO domandaDTO) {

        Optional<Utente> utenteOpt =
                utenteRepository.findByEmail(domandaDTO.getEmailUtente()); // controlla che esista l'utente

        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + domandaDTO.getEmailUtente());
        }


        Domanda domanda = new Domanda(utenteOpt.get(), domandaDTO.getArgomento(), domandaDTO.getTestoDomanda());

        domandaRepository.save(domanda);
    }
    
    public void deleteDomanda(int idDomanda) {
        Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
        if (domandaOpt.isPresent()) {
            domandaRepository.delete(domandaOpt.get());
        } else {
            throw new RuntimeException("Domanda non trovata con id: " + idDomanda);
        }
    }

    public void updateDomanda(int idDomanda, DomandaDTO domandaDTO) {
        Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
        if (domandaOpt.isPresent()) {
            Domanda domanda = domandaOpt.get();
            domanda.setArgomento(domandaDTO.getArgomento());
            domanda.setTestoDomanda(domandaDTO.getTestoDomanda());
            domandaRepository.save(domanda);
        } else {
            throw new RuntimeException("Domanda non trovata con ID: " + idDomanda);
        }
    }


    //per ottenere tutte le domande
    @Override
    public List<DomandaDTO> getTutteLeDomande() {
        List<Domanda> domande = domandaRepository.findAll();  // Recupera tutte le domande dal DB
        return domande.stream()
            .map(domanda -> new DomandaDTO(
                domanda.getIdDomanda(),
                domanda.getArgomento(),
                domanda.getTestoDomanda(),
                domanda.getUtente().getEmail()
                ))
            .collect(Collectors.toList());
    }



    public List<DomandaDTO> getDomandeByUtente(String emailUtente) {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(emailUtente);
        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + emailUtente);
        }

        return domandaRepository.findByUtente(utenteOpt.get()).stream()
                .map(domanda -> {

                    DomandaDTO dto = new DomandaDTO(
                            domanda.getIdDomanda(),
                            domanda.getArgomento(),
                            domanda.getTestoDomanda(),
                            domanda.getUtente().getEmail()
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }
}