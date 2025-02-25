package com.i_rosilients.backend.services.persistence;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.domanda.Opzione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpzioneRepository extends JpaRepository<Opzione, Integer> {
    void deleteByDomanda(Domanda domanda); // Per eliminare le opzioni quando si elimina una domanda
    List<Opzione> findByDomanda(Domanda domanda);
}
