package com.i_rosilients.backend.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.i_rosilients.backend.dto.QuestionarioCompilatoDTO;
import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.service.IQuestionarioCompilatoService;

@RestController
@RequestMapping("/api/questionariCompilati")
public class QuestionarioCompilatoController {

    private final IQuestionarioCompilatoService questionarioCompilatoService;

    public QuestionarioCompilatoController(IQuestionarioCompilatoService questionarioCompilatoService) {
        this.questionarioCompilatoService = questionarioCompilatoService;
    }

    @DeleteMapping("/deleteQuestionarioCompilato/{idCompilazione}")
    public ResponseEntity<String> deleteQuestionarioCompilatoAndRisposteByIdCompilazione(@PathVariable int idCompilazione) {
        try {
            questionarioCompilatoService.deleteQuestionarioCompilatoAndRisposteByIdCompilazione(idCompilazione);
            return ResponseEntity.ok("Questionario compilato eliminato con successo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore durante l'eliminazione del questionario compilato");
        }
    }

    @GetMapping("/{idCompilazione}")
    public ResponseEntity<QuestionarioCompilatoDTO> getQuestionarioCompilato(@PathVariable int idCompilazione) {
        QuestionarioCompilatoDTO questionarioCompilatoDTO = questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione);
        return ResponseEntity.ok(questionarioCompilatoDTO);
    }

    @GetMapping("/inSospeso/utente/{userEmail}")
    public ResponseEntity<List<QuestionarioCompilatoDTO>> getQuestionariCompilatiUtente(@PathVariable String userEmail) {
        List<QuestionarioCompilatoDTO> questionariCompilati = questionarioCompilatoService.getCompilazioniInSospeso(userEmail);
        if (questionariCompilati.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questionariCompilati);  
    }

    @GetMapping("/definitivi/utente/{userEmail}")
    public ResponseEntity<List<QuestionarioCompilatoDTO>> getDefinitiviByUtente(@PathVariable String userEmail) {
        List<QuestionarioCompilatoDTO> questionariCompilatiDefinitivi = questionarioCompilatoService.getDefinitiviByUtente(userEmail);
        
        if (questionariCompilatiDefinitivi.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(questionariCompilatiDefinitivi);
    }

    @GetMapping("/all/utente/{userEmail}")
    public ResponseEntity<List<QuestionarioCompilatoDTO>> getAllByUtente(@PathVariable String userEmail) {
        List<QuestionarioCompilatoDTO> questionariCompilati = questionarioCompilatoService.getAllByUtente(userEmail);
        
        if (questionariCompilati.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(questionariCompilati);
    }

    @GetMapping("/utenteNonRegistrato/{idCompilazione}")
    public ResponseEntity<QuestionarioCompilatoDTO> getQuestionarioCompilatoNonRegistrato(@PathVariable int idCompilazione) {
        QuestionarioCompilatoDTO questionarioCompilatoDTO = questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione);
        if (questionarioCompilatoDTO == null || !questionarioCompilatoService.checkEmailUtenteIsNullForQuestionario(idCompilazione)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(questionarioCompilatoDTO);
    }

    @GetMapping("/others/{userEmail}/{idQuestionario}")
    public ResponseEntity<List<QuestionarioCompilatoDTO>> getQuestionariCompilatiByUtenteAndQuestionario(@PathVariable String userEmail, @PathVariable int idQuestionario) {
        List<QuestionarioCompilatoDTO> questionariCompilati = questionarioCompilatoService.getQuestionariCompilatiByUtenteAndIdQuestionario(userEmail, idQuestionario);
        if (questionariCompilati.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questionariCompilati);
    }

    @GetMapping("/checkIsDefinitivo/{idCompilazione}")
    public ResponseEntity<Boolean> checkIsDefinitivo(@PathVariable int idCompilazione) {
        return ResponseEntity.ok(questionarioCompilatoService.checkIsDefinitivo(idCompilazione));
    }   

    @GetMapping("/{idCompilazione}/risposte")
    public ResponseEntity<List<RispostaDTO>> getRisposteByCompilazione(@PathVariable int idCompilazione) {
        List<RispostaDTO> risposte = questionarioCompilatoService.getRisposteByCompilazione(idCompilazione);
        
        if (risposte.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(risposte);
    }

    @PostMapping("/inviaEmail")
    public ResponseEntity<?> inviaEmail(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request) {
        try {
            String userEmail = (String) request.get("userCompilazioneToDelete");
            int idCompilazione = Integer.parseInt(request.get("compilazioneToDelete").toString());

            questionarioCompilatoService.inviaEmail(idCompilazione, userEmail);
    
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID compilazione non valido.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}