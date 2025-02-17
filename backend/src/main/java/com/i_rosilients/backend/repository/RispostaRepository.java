package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.Risposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RispostaRepository extends JpaRepository<Risposta, Integer> {
    
}