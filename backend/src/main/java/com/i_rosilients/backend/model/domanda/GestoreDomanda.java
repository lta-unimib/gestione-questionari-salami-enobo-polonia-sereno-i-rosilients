package com.i_rosilients.backend.model.domanda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.persistence.DomandaQuestionarioRepository;
import com.i_rosilients.backend.services.persistence.DomandaRepository;
import com.i_rosilients.backend.services.persistence.OpzioneRepository;
import com.i_rosilients.backend.services.persistence.UtenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GestoreDomanda implements IGestoreDomanda {

    private final DomandaRepository domandaRepository;
    private final UtenteRepository utenteRepository;
    private final OpzioneRepository opzioneRepository;
    private final DomandaQuestionarioRepository domandaQuestionarioRepository;

    public GestoreDomanda(DomandaRepository domandaRepository, UtenteRepository utenteRepository,
                          OpzioneRepository opzioneRepository, DomandaQuestionarioRepository domandaQuestionarioRepository) {
        this.domandaRepository = domandaRepository;
        this.utenteRepository = utenteRepository;
        this.opzioneRepository = opzioneRepository;
        this.domandaQuestionarioRepository = domandaQuestionarioRepository;
    }

    @Override
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

    @Override
public void updateDomanda(int idDomanda, DomandaDTO domandaDTO) {
    Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
    if (domandaOpt.isPresent()) {
        Domanda domanda = domandaOpt.get();
        domanda.setArgomento(domandaDTO.getArgomento());
        domanda.setTestoDomanda(domandaDTO.getTestoDomanda());

                String vecchiaImmaginePath = domanda.getImmaginePath(); 
        if (domandaDTO.isRemoveImage()) {
                        try {
                eliminaImmagine(vecchiaImmaginePath);
            } catch (IOException e) {
            }
            domanda.setImmaginePath(null);         } else if (domandaDTO.getImagePath() != null) {
                        try {
                eliminaImmagine(vecchiaImmaginePath);
            } catch (IOException e) {
            }
            domanda.setImmaginePath(domandaDTO.getImagePath());         }

        domandaRepository.save(domanda);

                aggiornaOpzioni(domanda, domandaDTO.getOpzioni());
    } else {
        throw new RuntimeException("Domanda non trovata con ID: " + idDomanda);
    }
}

    private void aggiornaOpzioni(Domanda domanda, List<String> nuoveOpzioni) {
        if (nuoveOpzioni != null) {
            List<Opzione> opzioniEsistenti = opzioneRepository.findByDomanda(domanda);

                        for (Opzione opzione : opzioniEsistenti) {
                if (!nuoveOpzioni.contains(opzione.getTestoOpzione())) {
                    opzioneRepository.delete(opzione);
                }
            }

                        for (String testoOpzione : nuoveOpzioni) {
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
    }

    @Override
    public String salvaImmagine(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path destinationPath = Paths.get("uploads").resolve(fileName).normalize();

        if (!destinationPath.startsWith(Paths.get("uploads").normalize())) {
            throw new IOException("Path traversal attempt detected");
        }

        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return "/api/domande/uploads/" + fileName;
    }

    @Override
    public void eliminaImmagine(String imagePath) throws IOException {
        if (imagePath != null) {
            String filename = new File(imagePath).getName();
            Path path = Paths.get("uploads").resolve(filename).normalize();
            if (!path.startsWith(Paths.get("uploads").normalize())) {
                throw new IOException("Invalid image path detected");
            }
            Files.deleteIfExists(path);
        }
    }

    @Override
    public List<String> parseOpzioniJson(String opzioniJson) throws JsonProcessingException {
        if (opzioniJson == null || opzioniJson.isEmpty()) {
            return new ArrayList<>();
        }
        return new ObjectMapper().readValue(opzioniJson, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
    }

    @Override
    @Transactional
    public void deleteDomanda(int idDomanda) {
        Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
        if (domandaOpt.isPresent()) {
            Domanda domanda = domandaOpt.get();

            if (domanda.getImmaginePath() != null && !domanda.getImmaginePath().isEmpty()) {
                try {
                    eliminaImmagine(domanda.getImmaginePath());
                } catch (IOException e) {
                    throw new RuntimeException("Errore durante la rimozione dell'immagine", e);
                }
            }

            domandaRepository.delete(domanda);
        } else {
            throw new RuntimeException("Domanda non trovata con id: " + idDomanda);
        }
    }

    @Override
    public List<DomandaDTO> getTutteLeDomande() {
        List<Domanda> domande = domandaRepository.findAll();
        return domande.stream()
                .map(domanda -> new DomandaDTO(
                        domanda.getIdDomanda(),
                        domanda.getArgomento(),
                        domanda.getTestoDomanda(),
                        domanda.getUtente().getEmail(),
                        domanda.getImmaginePath(),
                        false,
                        opzioneRepository.findByDomanda(domanda).stream()
                                .map(Opzione::getTestoOpzione)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DomandaDTO> getDomandeByUtente(String emailUtente) {
        Utente utente = utenteRepository.findByEmail(emailUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente con email " + emailUtente + " non trovato."));
        List<Domanda> domande = domandaRepository.findByUtente(utente);
        return domande.stream()
                .map(domanda -> new DomandaDTO(
                        domanda.getIdDomanda(),
                        domanda.getArgomento(),
                        domanda.getTestoDomanda(),
                        domanda.getUtente().getEmail(),
                        domanda.getImmaginePath(),
                        false,
                        opzioneRepository.findByDomanda(domanda).stream()
                                .map(Opzione::getTestoOpzione)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DomandaDTO> getDomandeByQuestionario(String idQuestionario) {
        try {
            Integer questionarioId = Integer.valueOf(idQuestionario);
            List<Integer> domandeIds = domandaQuestionarioRepository.findDomandeIdsByQuestionarioId(questionarioId);

            if (domandeIds.isEmpty()) {
                throw new RuntimeException("Nessuna domanda trovata per il questionario");
            }

            List<Domanda> domande = domandaRepository.findAllById(domandeIds);
            return domande.stream()
                    .map(domanda -> new DomandaDTO(
                            domanda.getIdDomanda(),
                            domanda.getArgomento(),
                            domanda.getTestoDomanda(),
                            domanda.getUtente().getEmail(),
                            domanda.getImmaginePath(),
                            false,
                            opzioneRepository.findByDomanda(domanda).stream()
                                    .map(Opzione::getTestoOpzione)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID del questionario non valido");
        }
    }

    @Override
    public ResponseEntity<Resource> getImage(String filename) throws IOException {
        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        if (!filePath.startsWith(Paths.get("uploads").normalize())) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = determineContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
}