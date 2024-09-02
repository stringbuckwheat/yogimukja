package com.memil.yogimukja.auth.service;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginRequest;

public interface AuthService {
    AuthTokens login(LoginRequest loginRequest);
    AuthTokens authenticateAndGenerateTokens(Long userId, String username);
    String reissueToken(String refreshToken);
    void removeAuthentication(String refreshToken);
}
