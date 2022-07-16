package com.youlearn.youlearn.service;

import com.youlearn.youlearn.model.Token;
import com.youlearn.youlearn.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    public Optional<Token> getToken(String tokenString) {
        return tokenRepository.findByToken(tokenString);
    }

    public void setConfirmedAt(String token) {
        tokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    public void deleteTokenByUserId(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }
}
