package com.i_rosilients.backend.model.domanda;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.DomandaQuestionarioRepository;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.OpzioneRepository;
import com.i_rosilients.backend.services.persistence.UtenteRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GestoreDomanda implements IGestoreDomanda {

 
    private final DomandaRepository domandaRepository;
    private final UtenteRepository utenteRepository;
    private final OpzioneRepository opzioneRepository;
    private final DomandaQuestionarioRepository domandaQuestionarioRepository;

    public GestoreDomanda(DomandaRepository domandaRepository, UtenteRepository utenteRepository, OpzioneRepository opzioneRepository, DomandaQuestionarioRepository domandaQuestionarioRepository){
        this.domandaRepository = domandaRepository;
        this.utenteRepository = utenteRepository;
        this.opzioneRepository = opzioneRepository;
        this.domandaQuestionarioRepository = domandaQuestionarioRepository;
    }

    public void creaDomanda(DomandaDTO domandaDTO) throws IOException {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(domandaDTO.getEmailUtente());
        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + domandaDTO.getEmailUtente());
        }

    
        Domanda domanda = new Domanda(utenteOpt.get(), domandaDTO.getArgomento(), domandaDTO.getTestoDomanda(), domandaDTO.getImagePath());
        domandaRepository.save(domanda);
    
        if (domandaDTO.getOpzioni() != null && !domandaDTO.getOpzioni().isEmpty()) {
            for (String testoOpzione : domandaDTO.getOpzioni()) {
                Opzione opzione = new Opzione();
                opzione.setTestoOpzione(testoOpzione);
                opzione.setDomanda(domanda);
                opzioneRepository.save(opzione);
            }
        }
    }

    public void updateDomanda(int idDomanda, DomandaDTO domandaDTO) {
        Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
        if (domandaOpt.isPresent()) {
            Domanda domanda = domandaOpt.get();
            domanda.setArgomento(domandaDTO.getArgomento());
            domanda.setTestoDomanda(domandaDTO.getTestoDomanda());

            // Aggiorna immagine
            if (domandaDTO.isRemoveImage()) {
                domanda.setImmaginePath(null);
            } else {
                domanda.setImmaginePath(domandaDTO.getImagePath());
            }

            domandaRepository.save(domanda);
    
            // Se sono state passate delle nuove opzioni, aggiorniamo le opzioni
            if (domandaDTO.getOpzioni() != null) {
                // Recupera tutte le opzioni esistenti per la domanda
                List<Opzione> opzioniEsistenti = opzioneRepository.findByDomanda(domanda);
    
                // Rimuovi opzioni che non sono più presenti
                for (Opzione opzione : opzioniEsistenti) {
                    if (!domandaDTO.getOpzioni().contains(opzione.getTestoOpzione())) {
                        opzioneRepository.delete(opzione);
                    }
                }
    
                // Aggiungi le nuove opzioni che non esistono già
                for (String testoOpzione : domandaDTO.getOpzioni()) {
                    boolean opzioneEsistente = opzioniEsistenti.stream()
                            .anyMatch(opzione -> opzione.getTestoOpzione().equals(testoOpzione));
                    
                    if (!opzioneEsistente) {
                        Opzione opzione = new Opzione();
                        opzione.setTestoOpzione(testoOpzione);
                        opzione.setDomanda(domanda);
                        opzioneRepository.save(opzione);
                    }
                }
            }
        } else {
            throw new RuntimeException("Domanda non trovata con ID: " + idDomanda);
        }
    }
    
    
    @Transactional
    public void deleteDomanda(int idDomanda) {
        Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
        if (domandaOpt.isPresent()) {
            Domanda domanda = domandaOpt.get();
            
            if (domanda.getImmaginePath() != null && !domanda.getImmaginePath().isEmpty()) {
                Path imagePath = Paths.get("uploads", new File(domanda.getImmaginePath()).getName());
                try {
                    Files.deleteIfExists(imagePath); // Elimina il file fisico se esiste
                } catch (IOException e) {
                    throw new RuntimeException("Errore durante la rimozione dell'immagine", e);
                }
            }
            domandaRepository.delete(domanda);
        } else {
            throw new RuntimeException("Domanda non trovata con id: " + idDomanda);
        }
    }


    //per ottenere tutte le domande
    @Override
    public List<DomandaDTO> getTutteLeDomande() {
        List<Domanda> domande = domandaRepository.findAll();  // Recupera tutte le domande dal DB
        
        // Crea la lista di DomandaDTO
        return domande.stream()
            .map(domanda -> {

                // Creazione del DomandaDTO
                return new DomandaDTO(
                    domanda.getIdDomanda(),
                    domanda.getArgomento(),
                    domanda.getTestoDomanda(),
                    domanda.getUtente().getEmail(),
                    domanda.getImmaginePath(),  // Immagine convertita in file
                    false,
                    opzioneRepository.findByDomanda(domanda).stream()
                        .map(Opzione::getTestoOpzione)
                        .collect(Collectors.toList())  // Se non ci sono opzioni, restituisce una lista vuota
                );
            })
            .collect(Collectors.toList());
    }



    public List<DomandaDTO> getDomandeByUtente(String emailUtente) {
        Utente utente = utenteRepository.findByEmail(emailUtente)
                                    .orElseThrow(() -> new IllegalArgumentException("Utente con email " + emailUtente + " non trovato."));
        List<Domanda> domande = domandaRepository.findByUtente(utente);
        // Crea la lista di DomandaDTO
        return domande.stream()
            .map(domanda -> {

                // Creazione del DomandaDTO
                return new DomandaDTO(
                    domanda.getIdDomanda(),
                    domanda.getArgomento(),
                    domanda.getTestoDomanda(),
                    domanda.getUtente().getEmail(),
                    domanda.getImmaginePath(),  // Immagine convertita in file
                    false,
                    opzioneRepository.findByDomanda(domanda).stream()
                        .map(Opzione::getTestoOpzione)
                        .collect(Collectors.toList())  // Se non ci sono opzioni, restituisce una lista vuota
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<DomandaDTO> getDomandeByQuestionario(String idQuestionario) {
        try {
            Integer questionarioId = Integer.valueOf(idQuestionario);  // Conversione della stringa a Integer
            List<Integer> domandeIds = domandaQuestionarioRepository.findDomandeIdsByQuestionarioId(questionarioId);

            if (domandeIds.isEmpty()) {
                throw new RuntimeException("Nessuna domanda trovata per il questionario");
            }

            // Trova le domande per gli ID ottenuti
            List<Domanda> domande = domandaRepository.findAllById(domandeIds);

            // Converti le domande in DomandaDTO
            return domande.stream()
            .map(domanda -> {

                // Creazione del DomandaDTO
                return new DomandaDTO(
                    domanda.getIdDomanda(),
                    domanda.getArgomento(),
                    domanda.getTestoDomanda(),
                    domanda.getUtente().getEmail(),
                    domanda.getImmaginePath(),  // Immagine convertita in file
                    false,
                    opzioneRepository.findByDomanda(domanda).stream()
                        .map(Opzione::getTestoOpzione)
                        .collect(Collectors.toList())  // Se non ci sono opzioni, restituisce una lista vuota
                );
            })
            .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID del questionario non valido");
        }
    }
}