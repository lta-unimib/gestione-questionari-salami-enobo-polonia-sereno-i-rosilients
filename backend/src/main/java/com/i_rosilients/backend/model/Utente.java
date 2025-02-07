package com.i_rosilients.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Utente implements UserDetails{


    @Id
    @Column(length = 255)
    private String email;

    @Getter
    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;
    private boolean enabled;


     //constructor for creating an unverified user
     public Utente(String email, String password) {
        this.email = email;
        this.password = password;
    }

 public Collection<? extends GrantedAuthority> getAuthorities() {
     return List.of();
  }

 //TODO: add proper boolean checks

 public boolean isAccountNonExpired() {
     return true;
 }

 public boolean isAccountNonLocked() {
     return true;
 }

 public boolean isCredentialsNonExpired() {
     return true;
 }

 
 public boolean isEnabled() {
     return enabled;
 }

@Override
public String getUsername() {
    return this.email;
}

}
