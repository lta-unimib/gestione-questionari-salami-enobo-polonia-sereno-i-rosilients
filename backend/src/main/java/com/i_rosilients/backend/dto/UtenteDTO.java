package com.i_rosilients.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtenteDTO {

    private String email;
    private String password;

    public UtenteDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UtenteDTO() {
        
    }
    
}