package com.memil.yogimukja.auth.service;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginRequest;

public interface AuthService {
    AuthTokens login(LoginRequest loginRequest);
    String reissueToken(String refreshToken);
    void removeAuthentication(String refreshToken);
}
