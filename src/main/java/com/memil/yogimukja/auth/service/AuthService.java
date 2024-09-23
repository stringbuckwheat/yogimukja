package com.memil.yogimukja.auth.service;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginRequest;
import com.memil.yogimukja.common.error.exception.RefreshTokenException;
import com.memil.yogimukja.user.entity.User;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.NoSuchElementException;

public interface AuthService {
    /**
     * 로그인
     * 아이디, 비밀번호 일치 확인 후 인증 토큰들(Access Token과 Refresh Token) 발급
     *
     * @param loginRequest 사용자의 로그인 정보(사용자명, 비밀번호)
     * @return 인증 토큰 정보(Access Token, Refresh Token, 사용자 이름)
     * @throws NoSuchElementException 사용자명을 찾을 수 없을 경우 발생
     * @throws BadCredentialsException 비밀번호가 일치하지 않을 경우 발생
     */
    AuthTokens login(LoginRequest loginRequest);

    /**
     * 주어진 사용자 정보로 인증 처리, Access Token과 Refresh Token 발급
     *
     * @param user 인증을 진행할 사용자 정보
     * @return 발급된 인증 토큰 정보(Access Token, Refresh Token, 사용자 이름)
     */
    AuthTokens authenticateAndGenerateTokens(User user);

    /**
     * Access Token 재발급
     *
     * @param refreshToken
     * @return 새 Access Token
     * @throws RefreshTokenException 주어진 Refresh Token이 유효하지 않거나 존재하지 않을 경우 발생
     */
    String reissueToken(String refreshToken);

    /**
     * 로그아웃
     * Refresh Token을 사용해 사용자 인증 정보 제거
     *
     * @param refreshToken 삭제할 인증 토큰
     * @throws RefreshTokenException 주어진 Refresh Token이 유효하지 않거나 존재하지 않을 경우 발생
     */
    void removeAuthentication(String refreshToken);
}
