package com.i_rosilients.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.i_rosilients.backend.model.Utente;

import java.util.Optional;

@Repository
public interface UtenteRepository extends CrudRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByVerificationCode(String verificationCode);
}