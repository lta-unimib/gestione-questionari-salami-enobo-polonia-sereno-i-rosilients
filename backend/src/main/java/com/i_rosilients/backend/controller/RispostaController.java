package com.i_rosilients.backend.controller;

import com.i_rosilients.backend.dto.RispostaDTO;
import com.i_rosilients.backend.service.IRispostaService;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risposte")
public class RispostaController {

    private final IRispostaService rispostaService;

    public RispostaController(IRispostaService rispostaService){
        this.rispostaService = rispostaService;
    }


    @PostMapping("/creaCompilazione")
    public ResponseEntity<?> creaCompilazione(@RequestParam int idQuestionario, @RequestParam String userEmail) {
        try {
            int idCompilazione = rispostaService.creaNuovaCompilazione(idQuestionario, userEmail);
            return ResponseEntity.ok().body(Map.of(
                    "idCompilazione", idCompilazione,
                    "message", "Compilazione creata con successo"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{idCompilazione}")
    public Map<Integer, String> getRisposteByIdCompilazione(@PathVariable int idCompilazione) {
        return rispostaService.getRisposteByIdCompilazione(idCompilazione);
    }

    // Endpoint per salvare una risposta
    @PostMapping("/salvaRisposta")
    public ResponseEntity<?> salvaRisposta(@RequestBody RispostaDTO rispostaDTO) {
        try {
            rispostaService.salvaRisposta(rispostaDTO);
            return ResponseEntity.ok().body(Map.of("message", "Risposta salvata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/finalizzaCompilazione")
    public ResponseEntity<?> finalizzaCompilazione(@RequestParam int idCompilazione) {
        try {
            rispostaService.finalizzaCompilazione(idCompilazione);

            return ResponseEntity.ok().body(Map.of("message", "Compilazione finalizzata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }    
    }

    @PostMapping("/inviaEmail")
    public ResponseEntity<?> inviaEmail(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request) {
        try {
            String userEmail = (String) request.get("userEmail");
            int idCompilazione = Integer.parseInt(request.get("idCompilazione").toString());
    
            rispostaService.inviaEmailConPdf(userEmail, idCompilazione);
    
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
}
