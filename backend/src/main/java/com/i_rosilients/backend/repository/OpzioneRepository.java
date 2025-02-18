package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.Domanda;
import com.i_rosilients.backend.model.Opzione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpzioneRepository extends JpaRepository<Opzione, Integer> {
    void deleteByDomanda(Domanda domanda); // Per eliminare le opzioni quando si elimina una domanda
    List<Opzione> findByDomanda(Domanda domanda);
}
