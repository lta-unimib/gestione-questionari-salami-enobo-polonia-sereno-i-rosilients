package com.i_rosilients.backend.service;

import com.i_rosilients.backend.model.Utente;

public interface UtenteService {

    void registraUtente(String email, String password);
    // void verificaEmail(String email, String tokenInserito);

}
