package com.i_rosilients.backend.services.session;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.i_rosilients.backend.dto.UtenteDTO;
import com.i_rosilients.backend.dto.VerificaUtenteDTO;
import com.i_rosilients.backend.model.utente.IGestoreUtente;
import com.i_rosilients.backend.model.utente.Utente;
import com.i_rosilients.backend.services.session.response.LoginResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class GestoreSessione implements IGestoreSessione{
    
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final IGestoreUtente gestoreUtente;

    public GestoreSessione(JwtService jwtService, AuthenticationService authenticationService, IGestoreUtente gestoreUtente) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.gestoreUtente = gestoreUtente;
    }

    @Override
    public Utente signup (UtenteDTO registerUtenteDto) {return authenticationService.signup(registerUtenteDto);}

    @Override
    public LoginResponse authenticate(UtenteDTO loginUtenteDto, HttpServletResponse response) {
    
        Utente authenticatedUtente = authenticationService.authenticate(loginUtenteDto);

        if (authenticatedUtente == null) {
            throw new RuntimeException("Utente non trovato");
        }

        String accessToken = jwtService.generateToken((UserDetails) authenticatedUtente);
        String refreshToken = jwtService.generateRefreshToken((UserDetails) authenticatedUtente);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); 
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);

        return new LoginResponse(accessToken, jwtService.getExpirationTime());   
    }
    
    @Override
    public LoginResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || jwtService.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Token Expired or null"); 
        }

        String email = jwtService.extractUsername(refreshToken);
        Utente utente = authenticationService.findUtenteByEmail(email);
        String newAccessToken = jwtService.generateToken((UserDetails) utente);

        return new LoginResponse(newAccessToken, jwtService.getExpirationTime());
    }

    @Override
    public String logout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(0);

        response.addCookie(refreshCookie);

        return "Logout effettuato con successo";
    }

    @Override
    public void verifyUtente(@RequestBody VerificaUtenteDTO verifyUtenteDto) {authenticationService.verifyUtente(verifyUtenteDto);}

    @Override
    public void resendVerificationCode(String email) {authenticationService.resendVerificationCode(email);}

    @Override
    public String deleteProfile(HttpServletRequest request) {
        String token = jwtService.extractToken(request);
        String username = jwtService.extractUsername(token);
        
        if (token == null || !jwtService.isTokenValid(token, username)) {
            throw new RuntimeException("Token Expired or null");
        }
        
        String emailUtente = jwtService.extractUsername(token);
        Utente utente = authenticationService.findUtenteByEmail(emailUtente);
        
        if (utente == null) {
            throw new RuntimeException("Utente non trovato");
        }
        
        try {
            gestoreUtente.deleteProfile(utente);

            return "Profilo eliminato con successo.";
        } catch (Exception e) {
            throw new RuntimeException("errore nell'eliminazione");
        }
    }
    

}
