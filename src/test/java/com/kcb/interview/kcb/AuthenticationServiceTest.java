package com.kcb.interview.kcb;

import com.kcb.interview.kcb.config.JwtTokenUtil;
import com.kcb.interview.kcb.config.JwtUserDetailsService;
import com.kcb.interview.kcb.config.RefreshTokenService;
import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.model.RefreshToken;
import com.kcb.interview.kcb.objects.ClientAuthRequest;
import com.kcb.interview.kcb.objects.AuthResponse;
import com.kcb.interview.kcb.objects.TokenRefreshResponse;
import com.kcb.interview.kcb.repository.ClientAuthRepository;
import com.kcb.interview.kcb.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private JwtUserDetailsService userDetailsService;

    @Mock
    private ClientAuthRepository clientAuthRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateClient_Success() throws Exception {

        ClientAuthRequest request = new ClientAuthRequest();
        request.setClientKey("clientKey");
        request.setClientSecret("clientSecret");

        ClientAuth clientAuth = new ClientAuth();
        clientAuth.setClientKey("clientKey");
        clientAuth.setClient("MyClient");
        clientAuth.setId(1);

        UserDetails userDetails = mock(UserDetails.class);
        when(clientAuthRepository.findClientAuthByClientKey("clientKey")).thenReturn(clientAuth);
        when(userDetailsService.loadUserByUsername("clientKey")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("accessToken");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshToken");
        when(refreshTokenService.createRefreshToken(1)).thenReturn(refreshToken);

        AuthResponse response = authenticationService.authenticateClient(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("MyClient", response.getClient());
    }

    @Test
    public void testRefreshToken_Success() {

        String requestRefreshToken = "refreshToken";
        ClientAuth clientAuth = new ClientAuth();
        clientAuth.setClientKey("clientKey");
        clientAuth.setId(1);

        RefreshToken existingRefreshToken = new RefreshToken();
        existingRefreshToken.setToken(requestRefreshToken);
        existingRefreshToken.setClientAuth(clientAuth);

        when(refreshTokenService.findByToken(requestRefreshToken))
                .thenReturn(java.util.Optional.of(existingRefreshToken));
        when(refreshTokenService.verifyExpiration(existingRefreshToken)).thenReturn(existingRefreshToken);
        when(jwtTokenUtil.generateTokenByUsername("clientKey")).thenReturn("newAccessToken");

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("newRefreshToken");
        when(refreshTokenService.createRefreshToken(1)).thenReturn(newRefreshToken);

        TokenRefreshResponse response = authenticationService.refreshToken(requestRefreshToken);


    }
}
