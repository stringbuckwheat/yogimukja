package com.memil.yogimukja.auth.dto;

import com.memil.yogimukja.user.entity.User;
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

    public AuthTokens(String refreshToken, String accessToken, User user) {
        this.refreshToken = refreshToken;
        this.loginResponse = new LoginResponse(accessToken, user);
    }
}
