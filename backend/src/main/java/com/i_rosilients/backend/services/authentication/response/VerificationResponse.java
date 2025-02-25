package com.i_rosilients.backend.services.authentication.response;

public class VerificationResponse {
    private String message;

    public VerificationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }   
}
