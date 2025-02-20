package com.i_rosilients.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateResetToken(String email) {
        try {
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(token, email, 15, TimeUnit.MINUTES); // Token valido per 15 minuti
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Impossibile connettersi a Redis", e);
        }
    }

    public boolean isResetTokenValid(String token) {
        try {
            return redisTemplate.hasKey(token);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile connettersi a Redis", e);
        }
    }

    public String getEmailFromResetToken(String token) {
        try {
            String email = redisTemplate.opsForValue().get(token);
            if (email == null) {
                throw new RuntimeException("Token non valido o scaduto");
            }
            return email;
        } catch (Exception e) {
            throw new RuntimeException("Impossibile connettersi a Redis", e);
        }
    }

    public void removeResetToken(String token) {
        try {
            redisTemplate.delete(token);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile connettersi a Redis", e);
        }
    }
}