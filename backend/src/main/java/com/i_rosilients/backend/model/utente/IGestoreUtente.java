package com.i_rosilients.backend.model.utente;

import java.util.List;

public interface IGestoreUtente {
    
    public List<Utente> allUsers();
    public void deleteProfile(Utente utente);

}
