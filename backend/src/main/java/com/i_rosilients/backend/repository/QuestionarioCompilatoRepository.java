package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.QuestionarioCompilato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionarioCompilatoRepository extends JpaRepository<QuestionarioCompilato, Integer> {
    
}