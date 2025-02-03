package com.i_rosilients.backend.service;

public interface UtenteService {

    void registraUtente(String email, String password);
    void verificaEmail(String email, String tokenInserito);

}
