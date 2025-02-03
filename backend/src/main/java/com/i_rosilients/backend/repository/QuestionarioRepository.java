package com.i_rosilients.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Utente;

@Repository
public interface QuestionarioRepository extends JpaRepository<Questionario, String> {
    List<Questionario> findByUtente(Utente utente);
}
