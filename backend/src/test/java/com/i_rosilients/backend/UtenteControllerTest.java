package com.i_rosilients.backend;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.i_rosilients.backend.controller.UtenteController;
import com.i_rosilients.backend.model.Utente;
import com.i_rosilients.backend.service.IUtenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
 class UtenteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUtenteService userService;

    @InjectMocks
    private UtenteController utenteController;

    @BeforeEach
     void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(utenteController).build();
    }

    @Test
     void testAuthenticatedUser() throws Exception {
        Utente utente = new Utente();
        utente.setEmail("test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(utente);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
     void testAllUsers() throws Exception {
        Utente utente1 = new Utente();
        utente1.setEmail("utente1@example.com");

        Utente utente2 = new Utente();
        utente2.setEmail("utente2@example.com");

        List<Utente> utenti = Arrays.asList(utente1, utente2);
        when(userService.allUsers()).thenReturn(utenti);
        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("utente1@example.com"))
                .andExpect(jsonPath("$[1].email").value("utente2@example.com"));
        verify(userService, times(1)).allUsers();
    }
}
