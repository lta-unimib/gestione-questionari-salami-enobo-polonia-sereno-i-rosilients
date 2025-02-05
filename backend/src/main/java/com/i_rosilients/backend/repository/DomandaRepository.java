package com.i_rosilients.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Utente;

@Repository
public interface DomandaRepository extends JpaRepository<Domanda, Integer> {
    List<Domanda> findByUtente(Utente utente);
}
