package com.i_rosilients.backend.response;

import lombok.Data;

@Data
public class VerificationResponse {
    private String message;
    private String status;

    public VerificationResponse(String message) {
        this.message = message;
    }
    public VerificationResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
 
}
