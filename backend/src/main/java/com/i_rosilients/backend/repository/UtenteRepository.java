package com.i_rosilients.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.i_rosilients.backend.model.Utente;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, String> {
    
}
