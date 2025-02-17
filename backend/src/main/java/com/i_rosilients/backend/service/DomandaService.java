package com.i_rosilients.backend.service;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Opzione;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.repository.OpzioneRepository;
import com.i_rosilients.backend.repository.UtenteRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DomandaService implements IDomandaService {

    @Autowired
    private DomandaRepository domandaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private OpzioneRepository opzioneRepository;

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

    public void updateDomanda(int idDomanda, DomandaDTO domandaDTO) {
        Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
        if (domandaOpt.isPresent()) {
            Domanda domanda = domandaOpt.get();
            domanda.setArgomento(domandaDTO.getArgomento());
            domanda.setTestoDomanda(domandaDTO.getTestoDomanda());

            // 👉 Controlliamo se l'utente ha richiesto la rimozione dell'immagine
            if (domandaDTO.isRemoveImage() && domanda.getImmaginePath() != null) {
                Path imagePath = Paths.get("uploads", new File(domanda.getImmaginePath()).getName());
                try {
                    Files.deleteIfExists(imagePath); // Elimina il file fisico
                    domanda.setImmaginePath(null); // Rimuove il riferimento nel database
                } catch (IOException e) {
                    throw new RuntimeException("Errore durante la rimozione dell'immagine", e);
                }
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
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(emailUtente);
        List<Domanda> domande = domandaRepository.findByUtente(utenteOpt.get());  // Recupera tutte le domande dal DB
        
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

    
}