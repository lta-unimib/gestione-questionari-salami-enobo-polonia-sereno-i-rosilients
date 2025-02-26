package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.DomandaDTO;
import com.i_rosilients.backend.model.domanda.IGestoreDomanda;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/domande")
public class DomandaController {

    private final IGestoreDomanda domandaService;

    public DomandaController(IGestoreDomanda domandaService) {
        this.domandaService = domandaService;
    }

    @PostMapping(value = "/creaDomanda", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> creaDomanda(
            @RequestParam("argomento") String argomento,
            @RequestParam("testoDomanda") String testoDomanda,
            @RequestParam("emailUtente") String emailUtente,
            @RequestParam(value = "opzioni", required = false) String opzioniJson,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            List<String> opzioni = domandaService.parseOpzioniJson(opzioniJson);
            String imagePath = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imagePath = domandaService.salvaImmagine(imageFile);
            }

            DomandaDTO domandaDTO = new DomandaDTO(argomento, testoDomanda, emailUtente, imagePath, opzioni);
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
            List<String> opzioni = domandaService.parseOpzioniJson(opzioniJson);
            String imagePath = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imagePath = domandaService.salvaImmagine(imageFile);
            }

            DomandaDTO domandaDTO = new DomandaDTO(argomento, testoDomanda, emailUtente, imagePath, opzioni);
            domandaDTO.setRemoveImage(removeImage);
            domandaService.updateDomanda(idDomanda, domandaDTO);

            return ResponseEntity.ok("Domanda aggiornata con successo");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'aggiornamento della domanda");
        }
    }

    @DeleteMapping("/deleteDomanda/{idDomanda}")
    public ResponseEntity<String> deleteDomanda(@PathVariable int idDomanda) {
        try {
            domandaService.deleteDomanda(idDomanda);
            return ResponseEntity.ok("Domanda eliminata con successo");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{emailUtente}")
    public ResponseEntity<List<DomandaDTO>> getDomandeByUtente(@PathVariable String emailUtente) {
        try {
            List<DomandaDTO> domande = domandaService.getDomandeByUtente(emailUtente);
            return ResponseEntity.ok(domande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tutteLeDomande")
    public ResponseEntity<List<DomandaDTO>> getTutteLeDomande() {
        List<DomandaDTO> domande = domandaService.getTutteLeDomande();
        return ResponseEntity.ok(domande);
    }

    @GetMapping("/domandeByQuestionario/{idQuestionario}")
    public ResponseEntity<List<DomandaDTO>> getDomandeByQuestionario(@PathVariable String idQuestionario) {
        try {
            List<DomandaDTO> domande = domandaService.getDomandeByQuestionario(idQuestionario);
            return ResponseEntity.ok(domande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        return domandaService.getImage(filename);
    }
}