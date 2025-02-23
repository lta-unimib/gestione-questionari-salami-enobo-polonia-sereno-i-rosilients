package com.i_rosilients.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.repository.DomandaRepository;
import com.i_rosilients.backend.service.IDomandaService;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/domande")
public class DomandaController {

    private final IDomandaService domandaService;
    private final DomandaRepository domandaRepository;
    private static final String UPLOAD_DIR = "uploads/";
    private static final Path targetPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

    
    public DomandaController(IDomandaService domandaService, DomandaRepository domandaRepository) {
        this.domandaService = domandaService;
        this.domandaRepository = domandaRepository;
        init();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }


    @PostMapping(value = "/creaDomanda", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> creaDomanda(
            @RequestParam("argomento") String argomento,
            @RequestParam("testoDomanda") String testoDomanda,
            @RequestParam("emailUtente") String emailUtente,
            @RequestParam(value = "opzioni", required = false) String opzioniJson,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            // Converti la lista di opzioni da JSON a List<String>
            List<String> opzioni = new ArrayList<>();
            if (opzioniJson != null && !opzioniJson.isEmpty()) {
                opzioni = new ObjectMapper().readValue(opzioniJson, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
            }

            String imagePath = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imagePath = salvaImmagine(imageFile);
            }

            // Creiamo il DTO con l'immagine
            DomandaDTO domandaDTO = new DomandaDTO(argomento, testoDomanda, emailUtente, imagePath, opzioni);

            // Passiamo il DTO al service
            domandaService.creaDomanda(domandaDTO);


            return ResponseEntity.ok("Domanda creata con successo");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nel salvataggio dell'immagine");
        }
    }

    @PutMapping(value = "/updateDomanda/{idDomanda}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateDomanda(
        @PathVariable int idDomanda, 
        @RequestParam("argomento") String argomento,
        @RequestParam("testoDomanda") String testoDomanda,
        @RequestParam("emailUtente") String emailUtente,
        @RequestParam(value = "opzioni", required = false) String opzioniJson,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage) {
        
        try {
            Optional<Domanda> domandaOpt = domandaRepository.findById(idDomanda);
            if (domandaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Domanda domanda = domandaOpt.get();
            
            List<String> opzioni = new ArrayList<>();
            if (opzioniJson != null && !opzioniJson.isEmpty()) {
                try {
                    opzioni = new ObjectMapper().readValue(opzioniJson, 
                        new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                } catch (JsonProcessingException e) {
                    return ResponseEntity.badRequest().body("Formato opzioni non valido");
                }
            }

            String imageUrl = domanda.getImmaginePath();
            
            if (removeImage) {
                if (imageUrl != null) {
                    try {
                        String filename = new File(imageUrl).getName();
                        Path imagePath = targetPath.resolve(filename).normalize();
                        
                        if (!imagePath.startsWith(targetPath)) {
                            throw new IOException("Invalid image path detected");
                        }
                        
                        Files.deleteIfExists(imagePath);
                        imageUrl = null;
                    } catch (IOException e) {
                        // Logger.error("Errore durante la rimozione del file", e);
                    }
                }
            } else if (imageFile != null && !imageFile.isEmpty()) {
                // Rimuovi il vecchio file se esiste
                if (imageUrl != null) {
                    try {
                        String oldFilename = new File(imageUrl).getName();
                        Path oldImagePath = targetPath.resolve(oldFilename).normalize();
                        
                        if (!oldImagePath.startsWith(targetPath)) {
                            throw new IOException("Invalid old image path detected");
                        }
                        
                        Files.deleteIfExists(oldImagePath);
                    } catch (IOException e) {
                        // Logger.error("Errore durante la rimozione del vecchio file", e);
                    }
                }
                
                try {
                    imageUrl = salvaImmagine(imageFile);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Errore durante il salvataggio della nuova immagine");
                }
            }
            DomandaDTO domandaDTO = new DomandaDTO(argomento, testoDomanda, emailUtente, imageUrl, opzioni);
            domandaService.updateDomanda(idDomanda, domandaDTO);

            return ResponseEntity.ok("Domanda aggiornata con successo");
            
        } catch (Exception e) {
            // Logger.error("Errore durante l'aggiornamento della domanda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore durante l'aggiornamento della domanda: " + e.getMessage());
        }
    }

     private String salvaImmagine(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path destinationPath = targetPath.resolve(fileName).normalize();

        if (!destinationPath.startsWith(targetPath)) {
            throw new IOException("Path traversal attempt detected");
        }

        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return "/api/domande/uploads/" + fileName;
    }

    @DeleteMapping("/deleteDomanda/{idDomanda}")
    public void deleteDomanda(@PathVariable int idDomanda) {
        try {
            domandaService.deleteDomanda(idDomanda);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{emailUtente}")
    public List<DomandaDTO> getDomandeByUtente(@PathVariable String emailUtente) {
        return domandaService.getDomandeByUtente(emailUtente);
    }


    //per ottenere tutte le domande
    @GetMapping("/tutteLeDomande")
    public List<DomandaDTO> getTutteLeDomande() {
        return domandaService.getTutteLeDomande();
    }

    //per ottenere le domande associate ad un questionario
    @GetMapping("/domandeByQuestionario/{idQuestionario}")
    public ResponseEntity<List<DomandaDTO>> getDomandeByQuestionario(@PathVariable String idQuestionario) {
        try {
            List<DomandaDTO> domande = domandaService.getDomandeByQuestionario(idQuestionario);
            return ResponseEntity.ok(domande);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        // Sanitizza e verifica il filename
        Path filePath = targetPath.resolve(filename).normalize();
        
        // Verifica che il path risultante sia dentro la directory target
        if (!filePath.startsWith(targetPath)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Determina il content type in modo sicuro
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public String determineContentType(String filename) {
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