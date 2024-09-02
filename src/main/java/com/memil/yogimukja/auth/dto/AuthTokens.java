package com.memil.yogimukja.auth.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * AuthController - AuthService 레이어 간 사용하는 DTO
 */
@Getter
@ToString
public class AuthTokens {
    private String refreshToken;
    private LoginResponse loginResponse;

    public AuthTokens(String refreshToken, String accessToken, String username) {
        this.refreshToken = refreshToken;
        this.loginResponse = new LoginResponse(username, accessToken);
    }
}
