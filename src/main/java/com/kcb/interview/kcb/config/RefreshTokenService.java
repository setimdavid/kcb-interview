package com.kcb.interview.kcb.config;


import com.kcb.interview.kcb.model.RefreshToken;
import com.kcb.interview.kcb.repository.ClientAuthRepository;
import com.kcb.interview.kcb.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RefreshTokenService {
    public static final long JWT_TOKEN_VALIDITY = 60 * 10 * 1000;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ClientAuthRepository clientAuthRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Integer userId) {
        AtomicReference<RefreshToken> ref = new AtomicReference<>(new RefreshToken());
        refreshTokenRepository.findRefreshTokenByClientAuth(clientAuthRepository.findById(userId).get())
                .ifPresentOrElse(refreshToken -> {
                    refreshToken.setExpiryDate(Timestamp.from(Instant.now().plusMillis(JWT_TOKEN_VALIDITY)));
                    refreshToken.setToken(UUID.randomUUID().toString());
                    refreshToken = refreshTokenRepository.save(refreshToken);
                    ref.set(refreshToken);
                }, () -> {
                    RefreshToken refreshToken = new RefreshToken();
                    refreshToken.setClientAuth(clientAuthRepository.findById(userId).get());
                    refreshToken.setExpiryDate(Timestamp.from(Instant.now().plusMillis(JWT_TOKEN_VALIDITY)));
                    refreshToken.setToken(UUID.randomUUID().toString());
                    refreshToken.setCreatedDate(Timestamp.from(Instant.now()));
                    refreshToken = refreshTokenRepository.save(refreshToken);
                    ref.set(refreshToken);
                });
        return ref.get();
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Timestamp.from(Instant.now())) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByClientId(Integer clientId) {
        return refreshTokenRepository.deleteRefreshTokenByClientAuth(clientAuthRepository.findById(clientId).get());
    }
}
