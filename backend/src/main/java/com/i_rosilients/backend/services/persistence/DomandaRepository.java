package com.i_rosilients.backend.services.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.utente.Utente;

@Repository
public interface DomandaRepository extends JpaRepository<Domanda, Integer> {
    
    List<Domanda> findByUtente(Utente utente);

    @Override
    List<Domanda> findAll();
    
    void deleteAllByUtente(Utente utente);
    
    @Query("SELECT dq.idDomanda FROM DomandaQuestionario dq WHERE dq.questionario.id = :questionarioId")
    List<Integer> findDomandeIdsByQuestionarioId(@Param("questionarioId") int questionarioId);
}
