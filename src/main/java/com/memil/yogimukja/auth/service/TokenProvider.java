package com.memil.yogimukja.auth.service;

import com.memil.yogimukja.auth.token.AccessToken;
import com.memil.yogimukja.auth.token.RefreshToken;

public interface TokenProvider {
    AccessToken convertAccessToken(String token);
    void setAuthentication(Long userId, String username);
    AccessToken generateAccessToken(Long userId, String username);
    RefreshToken generateRefreshToken(Long userId, String username);
    AccessToken refreshAccessToken(String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
