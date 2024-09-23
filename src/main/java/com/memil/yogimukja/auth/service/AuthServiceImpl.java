package com.memil.yogimukja.auth.service;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginRequest;
import com.memil.yogimukja.auth.token.AccessToken;
import com.memil.yogimukja.auth.token.RefreshToken;
import com.memil.yogimukja.common.error.ErrorMessage;
import com.memil.yogimukja.common.error.exception.RefreshTokenException;
import com.memil.yogimukja.user.entity.User;
import com.memil.yogimukja.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthTokens login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.WRONG_USERNAME.getMessage()));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorMessage.WRONG_PASSWORD.getMessage());
        }

        return authenticateAndGenerateTokens(user);
    }

    @Override
    public AuthTokens authenticateAndGenerateTokens(User user) {
        Long userId = user.getId();
        String username = user.getUsername();

        // Security Context 저장
        tokenProvider.setAuthentication(userId, username);

        // Access Token, Refresh Token 생성
        AccessToken accessToken = tokenProvider.generateAccessToken(userId, username);
        RefreshToken refreshToken = tokenProvider.generateRefreshToken(userId, username);

        return new AuthTokens(refreshToken.getToken(), accessToken.getToken(), user);
    }

    @Override
    @Transactional(readOnly = true)
    public String reissueToken(String refreshToken) {
        return tokenProvider.refreshAccessToken(refreshToken).getToken();
    }

    @Override
    @Transactional
    public void removeAuthentication(String refreshToken) {
        if(!StringUtils.hasText(refreshToken)) {
            throw new RefreshTokenException(ErrorMessage.NO_REFRESH_TOKEN.getMessage());
        }

        tokenProvider.deleteRefreshToken(refreshToken);
    }
}
