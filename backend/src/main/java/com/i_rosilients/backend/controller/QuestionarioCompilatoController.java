package com.i_rosilients.backend.controller;
import java.util.List;

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

    @GetMapping("/utenteNonRegistrato/{idCompilazione}")
    public ResponseEntity<QuestionarioCompilatoDTO> getQuestionarioCompilatoNonRegistrato(@PathVariable int idCompilazione) {
        QuestionarioCompilatoDTO questionarioCompilatoDTO = questionarioCompilatoService.getQuestionarioCompilatoById(idCompilazione);
        if (questionarioCompilatoDTO == null || !questionarioCompilatoService.checkEmailUtenteIsNullForQuestionario(idCompilazione)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(questionarioCompilatoDTO);
    }

    @GetMapping("/checkIsDefinitivo/{idCompilazione}")
    public ResponseEntity<Boolean> checkIsDefinitivo(@PathVariable int idCompilazione) {
        return ResponseEntity.ok(questionarioCompilatoService.checkIsDefinitivo(idCompilazione));
    }

    @GetMapping("/utente/{userEmail}")
    public List<QuestionarioCompilatoDTO> getQuestionariCompilatiUtente(@PathVariable String userEmail) {
        return questionarioCompilatoService.getCompilazioniInSospeso(userEmail);  
    }

    @GetMapping("/{idCompilazione}/risposte")
public ResponseEntity<List<RispostaDTO>> getRisposteByCompilazione(@PathVariable int idCompilazione) {
    List<RispostaDTO> risposte = questionarioCompilatoService.getRisposteByCompilazione(idCompilazione);
    
    if (risposte.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(risposte);
    }

    @GetMapping("/definitivi/utente/{userEmail}")
    public ResponseEntity<List<QuestionarioCompilatoDTO>> getDefinitiviByUtente(@PathVariable String userEmail) {
        List<QuestionarioCompilatoDTO> questionariCompilatiDefinitivi = questionarioCompilatoService.getDefinitiviByUtente(userEmail);
        
        if (questionariCompilatiDefinitivi.isEmpty()) {
            return ResponseEntity.noContent().build(); // Se non ci sono compilazioni definitive
        }
        
        return ResponseEntity.ok(questionariCompilatiDefinitivi);
    }

}