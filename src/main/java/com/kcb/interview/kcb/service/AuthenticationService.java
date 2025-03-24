package com.kcb.interview.kcb.service;

import com.kcb.interview.kcb.config.JwtTokenUtil;
import com.kcb.interview.kcb.config.JwtUserDetailsService;
import com.kcb.interview.kcb.config.RefreshTokenService;
import com.kcb.interview.kcb.config.TokenRefreshException;
import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.model.RefreshToken;
import com.kcb.interview.kcb.objects.AuthResponse;
import com.kcb.interview.kcb.objects.ClientAuthRequest;
import com.kcb.interview.kcb.objects.TokenRefreshResponse;
import com.kcb.interview.kcb.repository.ClientAuthRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Log
@Service
public class AuthenticationService {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final ClientAuthRepository clientAuthRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthenticationService(JwtTokenUtil jwtTokenUtil,
                                 JwtUserDetailsService userDetailsService,
                                 AuthenticationManager authenticationManager,
                                 ClientAuthRepository clientAuthRepository,
                                 RefreshTokenService refreshTokenService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.clientAuthRepository = clientAuthRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse authenticateClient(ClientAuthRequest clientAuthRequest) throws Exception {
        // Authenticate credentials
        authenticate(clientAuthRequest.getClientKey(), clientAuthRequest.getClientSecret());

        // Load client details and generate tokens
        ClientAuth clientAuth = clientAuthRepository.findClientAuthByClientKey(clientAuthRequest.getClientKey());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(clientAuth.getClientKey());
        String token = jwtTokenUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(clientAuth.getId());

        return new AuthResponse(token, refreshToken.getToken(), clientAuth.getClient());
    }

    public TokenRefreshResponse refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getClientAuth)
                .map(clientAuth -> {
                    String token = jwtTokenUtil.generateTokenByUsername(clientAuth.getClientKey());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(clientAuth.getId());
                    return new TokenRefreshResponse(token, newRefreshToken.getToken());
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
