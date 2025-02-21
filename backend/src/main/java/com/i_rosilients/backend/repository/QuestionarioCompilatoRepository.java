package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.QuestionarioCompilato;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionarioCompilatoRepository extends JpaRepository<QuestionarioCompilato, Integer> {
    void deleteByQuestionario(Questionario questionario);
    void deleteByIdCompilazione(int idCompilazione);
    List<QuestionarioCompilato> findByQuestionario(Questionario questionario);
    List<QuestionarioCompilato> findByUtenteEmailAndDefinitivoFalse(String email);
}