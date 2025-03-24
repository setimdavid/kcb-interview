package com.kcb.interview.kcb.controller;

import com.kcb.interview.kcb.config.TokenRefreshException;
import com.kcb.interview.kcb.objects.AuthResponse;
import com.kcb.interview.kcb.objects.ClientAuthRequest;
import com.kcb.interview.kcb.objects.TokenRefreshResponse;
import com.kcb.interview.kcb.service.AuthenticationService;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
@RestController
@RequestMapping({"/api/v1"})
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> clientAuthToken(@RequestBody ClientAuthRequest clientAuthRequest) {
        JSONObject responseMap = new JSONObject();
        try {
            AuthResponse authResponse = authenticationService.authenticateClient(clientAuthRequest);
            responseMap.put("access_token", authResponse.getAccessToken());
            responseMap.put("refresh_token", authResponse.getRefreshToken());
            responseMap.put("client", authResponse.getClient());
            return ResponseEntity.status(HttpStatus.OK).body(responseMap.toString());
        } catch (Exception e) {
            log.warning("Failed Authentication: " + e.getMessage());
            responseMap.put("status", "99");
            responseMap.put("description", "Failed Authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap.toString());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String requestBody) {
        JSONObject jsonObject = new JSONObject(requestBody);
        String requestRefreshToken = jsonObject.getString("refreshToken");
        try {
            TokenRefreshResponse tokenRefreshResponse = authenticationService.refreshToken(requestRefreshToken);
            return ResponseEntity.ok(tokenRefreshResponse);
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
