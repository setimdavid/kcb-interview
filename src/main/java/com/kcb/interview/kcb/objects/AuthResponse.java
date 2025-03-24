package com.kcb.interview.kcb.objects;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String client;

    public AuthResponse(String accessToken, String refreshToken, String client) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.client = client;
    }

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public String getClient() {
        return client;
    }
    public void setClient(String client) {
        this.client = client;
    }
}
