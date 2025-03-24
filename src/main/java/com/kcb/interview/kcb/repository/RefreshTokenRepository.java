package com.kcb.interview.kcb.repository;



import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findRefreshTokenByClientAuth(ClientAuth user);
    @Modifying
    int deleteRefreshTokenByClientAuth(ClientAuth user);

}
