package com.i_rosilients.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.i_rosilients.backend.dto.QuestionarioDTO;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;

@Repository
public interface QuestionarioRepository extends JpaRepository<Questionario, Integer> {

    List<Questionario> findByUtente(Utente utente);
    
    @SuppressWarnings("null")
    List<Questionario> findAll();
    
    @Query("SELECT new com.i_rosilients.backend.dto.QuestionarioDTO(q.idQuestionario, q.nome, u.email) " +
        "FROM Questionario q " +
        "JOIN q.utente u " +
        "WHERE q.nome LIKE %:nome% " +
        "AND EXISTS (SELECT dq FROM DomandaQuestionario dq WHERE dq.questionario.id = q.id)")
    List<QuestionarioDTO> searchQuestionariWithQuestions(@Param("nome") String nome);

    @Query("SELECT dq.idDomanda FROM DomandaQuestionario dq WHERE dq.questionario.id = :questionarioId")
    List<Integer> findDomandeIdsByQuestionarioId(@Param("questionarioId") int questionarioId);


    void deleteAllByUtente(Utente utente);
}
