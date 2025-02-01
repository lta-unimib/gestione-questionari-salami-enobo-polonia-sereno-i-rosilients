package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class Utente {

    @Id
    @Column(length = 255)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

}
