package com.i_rosilients.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.service.IDomandaService;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/domande")
public class DomandaController {

    @Autowired
    private IDomandaService domandaService;

    private static final String UPLOAD_DIR = "uploads/";


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

    private String salvaImmagine(MultipartFile file) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();  // Crea la cartella se non esiste
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, file.getBytes());

        return "/api/domande/uploads/" + fileName; // Percorso accessibile dal frontend
    }

    @DeleteMapping("/deleteDomanda/{idDomanda}")
    public void deleteDomanda(@PathVariable int idDomanda) {
        try {
            domandaService.deleteDomanda(idDomanda);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updateDomanda/{idDomanda}")
    public void updateDomanda(@PathVariable int idDomanda, @RequestBody DomandaDTO domandaDTO) {
        try {
            domandaService.updateDomanda(idDomanda, domandaDTO);
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

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        try {
            Path filePath = Paths.get("uploads").toAbsolutePath().resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
    
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
    
            // Determina il tipo MIME dinamicamente
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {

                if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (filename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "application/octet-stream"; // Default in caso di errore
                }
            }
    
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}