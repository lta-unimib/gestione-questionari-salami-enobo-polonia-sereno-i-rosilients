package com.i_rosilients.backend.dto;

import lombok.Getter;

@Getter
public class UtenteDTO {

    private String email;
    private String password;
    private boolean attivo;

    public UtenteDTO() { 
        this.attivo = false;
    }
    
    public UtenteDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.attivo = false;
    }
}
