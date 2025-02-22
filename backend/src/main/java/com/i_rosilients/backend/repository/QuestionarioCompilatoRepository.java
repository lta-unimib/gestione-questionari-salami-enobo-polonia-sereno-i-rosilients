package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionarioCompilatoRepository extends JpaRepository<QuestionarioCompilato, Integer> {

    void deleteByQuestionario(Questionario questionario);

    void deleteByIdCompilazione(int idCompilazione);

    List<QuestionarioCompilato> findByQuestionario(Questionario questionario);

    Optional<QuestionarioCompilato> findByIdCompilazione(int idCompilazione);

    List<QuestionarioCompilato> findByUtenteEmailAndDefinitivoFalse(String email);

    List<QuestionarioCompilato> findByUtenteEmailAndDefinitivoTrue(String email);

    @Query("SELECT qc FROM QuestionarioCompilato qc " +
       "WHERE (qc.utente.email IS NULL OR qc.utente.email <> :email) " +
       "AND qc.questionario.idQuestionario = :idQuestionario " + 
       "AND qc.definitivo = true")
    List<QuestionarioCompilato> findByUtenteEmailNotOrNullAndQuestionarioIdQuestionarioAndDefinitivo(
        @Param("email") String email,
        @Param("idQuestionario") int idQuestionario
    );
}