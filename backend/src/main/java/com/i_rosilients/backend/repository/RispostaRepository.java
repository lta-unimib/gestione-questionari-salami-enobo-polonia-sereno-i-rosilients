package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.QuestionarioCompilato;
import com.i_rosilients.backend.model.Risposta;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RispostaRepository extends JpaRepository<Risposta, Integer> {
     
     void deleteByQuestionarioCompilato_IdCompilazione(int idCompilazione);

     Optional<Risposta> findByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(int idCompilazione, int idDomanda);

   //  @Query("DELETE FROM Risposta r WHERE r.questionarioCompilato.idCompilazione = :idCompilazione AND r.domanda.idDomanda = :idDomanda")
   //  void deleteByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(@Param("idCompilazione") int idCompilazione, @Param("idDomanda") int idDomanda);

   @Modifying
   @Transactional
   @Query(value = "DELETE FROM risposta WHERE id_compilazione = :idCompilazione AND id_domanda = :idDomanda", nativeQuery = true)
   void deleteByQuestionarioCompilato_IdCompilazioneAndDomanda_IdDomanda(@Param("idCompilazione") int idCompilazione, @Param("idDomanda") int idDomanda);


     List<Risposta> findByQuestionarioCompilato_IdCompilazione(int idCompilazione);
     List<Risposta> findByQuestionarioCompilato(QuestionarioCompilato compilato);
     
}