package com.i_rosilients.backend.services.session;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.session.response.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IGestoreSessione {

    public Utente signup (UtenteDTO registerUtenteDto);

    public LoginResponse authenticate(UtenteDTO loginUtenteDto, HttpServletResponse response);

    public LoginResponse refresh(HttpServletRequest request, HttpServletResponse response);

    public String logout(HttpServletResponse response);

    public void verifyUtente(VerificaUtenteDTO verifyUtenteDto);

    public void resendVerificationCode(String email);

    public String deleteProfile(HttpServletRequest request);
    
}
