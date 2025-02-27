package com.i_rosilients.backend.services.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.i_rosilients.backend.model.utente.Utente;

import java.util.Optional;

@Repository
public interface UtenteRepository extends CrudRepository<Utente, String> {
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByVerificationCode(String verificationCode);
    void deleteByEmail(String email);
}