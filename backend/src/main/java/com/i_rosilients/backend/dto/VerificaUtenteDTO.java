package com.i_rosilients.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificaUtenteDTO {
    private String email;
    private String verificationCode;
}