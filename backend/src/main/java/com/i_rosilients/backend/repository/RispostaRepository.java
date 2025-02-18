package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.Risposta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RispostaRepository extends JpaRepository<Risposta, Integer> {
     
     void deleteByQuestionarioCompilato_IdCompilazione(int idCompilazione);

     List<Risposta> findByQuestionarioCompilato_IdCompilazione(int idCompilazione);

}