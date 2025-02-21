package com.i_rosilients.backend;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.i_rosilients.backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
 class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    private String supportEmail = "support@example.com";

    @BeforeEach
     void setup() {
        emailService = new EmailService(emailSender, supportEmail);
    }

    @Test
     void testSendVerificationEmail() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        String to = "user@example.com";
        String subject = "Verifica Email";
        String text = "Clicca sul link per verificare la tua email.";
        emailService.sendVerificationEmail(to, subject, text);
        verify(emailSender, times(1)).createMimeMessage();
        verify(emailSender, times(1)).send(mimeMessage);
    }

    @Test
     void testSendVerificationEmail_ThrowsMessagingException() {
        when(emailSender.createMimeMessage()).thenThrow(new RuntimeException("Errore di invio email"));
        String to = "user@example.com";
        String subject = "Verifica Email";
        String text = "Clicca sul link per verificare la tua email.";
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendVerificationEmail(to, subject, text);
        });
        assertEquals("Errore di invio email", exception.getMessage());
        verify(emailSender, times(1)).createMimeMessage();
    }
}
