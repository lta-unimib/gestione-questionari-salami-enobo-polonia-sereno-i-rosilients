package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Utente {

    @Id
    @Column(length = 255)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;
    
    @Column(nullable = false)
    private boolean attivo = false;
}
