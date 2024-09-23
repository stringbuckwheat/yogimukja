package com.memil.yogimukja.auth.service;

import com.memil.yogimukja.auth.token.AccessToken;
import com.memil.yogimukja.auth.token.RefreshToken;
import com.memil.yogimukja.common.error.exception.RefreshTokenException;

import java.util.NoSuchElementException;

public interface TokenProvider {
    /**
     * Jwt Access Token 문자열을 Access Token 객체로 변환
     * @param token JWT 토큰 문자열
     * @return 변환된 AccessToken 객체
     */
    AccessToken convertAccessToken(String token);

    /**
     * 주어진 사용자 정보를 통해 Security Context에 Authentication 설정
     *
     * @param userId   사용자 PK
     * @param username 사용자 이름
     */
    void setAuthentication(Long userId, String username);

    /**
     * 사용자 정보를 기반으로 Access Token 생성
     *
     * @param userId   사용자 ID
     * @param username 사용자 이름
     * @return 생성된 AccessToken 객체
     */
    AccessToken generateAccessToken(Long userId, String username);

    /**
     * 사용자 정보를 기반으로 Refresh Token 생성, Redis 저장
     *
     * @param userId   사용자 ID
     * @param username 사용자 이름
     * @return 생성된 RefreshToken 객체
     */
    RefreshToken generateRefreshToken(Long userId, String username);

    /**
     * Refresh Token을 사용해 Access Token 재발급
     *
     * @param refreshToken 재발급에 사용할 Refresh Token
     * @return 새로 발급된 AccessToken 객체
     * @throws NoSuchElementException Redis에 해당 Refresh Token이 존재하지 않는 경우
     * @throws RefreshTokenException Refresh Token이 만료된 경우
     */
    AccessToken refreshAccessToken(String refreshToken);

    /**
     * 주어진 Refresh Token을 Redis에서 삭제
     *
     * @param refreshToken 삭제할 Refresh Token
     */
    void deleteRefreshToken(String refreshToken);
}
