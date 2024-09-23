package com.memil.yogimukja.auth.controller;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginRequest;
import com.memil.yogimukja.auth.dto.LoginResponse;
import com.memil.yogimukja.auth.service.AuthService;
import com.memil.yogimukja.common.error.exception.RefreshTokenException;
import com.memil.yogimukja.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    /**
     * 로그인
     *
     * @param loginRequest 사용자의 로그인 정보(사용자명, 비밀번호)
     * @return 인증 토큰 정보(Access Token, Refresh Token, 사용자 이름)
     * @throws NoSuchElementException 사용자명을 찾을 수 없을 경우 발생
     * @throws BadCredentialsException 비밀번호가 일치하지 않을 경우 발생
     */
    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 로그인 서비스 호출
        AuthTokens authTokens = authService.login(loginRequest);

        // 쿠키에 Refresh token 담기
        HttpHeaders headers = cookieUtil.getHeaderWithRefreshToken(authTokens.getRefreshToken(), 4 * 60 * 60);

        return ResponseEntity.ok().headers(headers).body(authTokens.getLoginResponse());
    }

    /**
     * Access Token 재발급
     *
     * @param refreshToken
     * @return 새 Access Token
     * @throws RefreshTokenException 주어진 Refresh Token이 유효하지 않거나 존재하지 않을 경우 발생
     */
    @PostMapping("/api/token")
    public ResponseEntity<String> reissueToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken){
        System.out.println(refreshToken);
        return ResponseEntity.ok().body(authService.reissueToken(refreshToken));
    }

    /**
     * 로그아웃
     * Refresh Token을 사용해 사용자 인증 정보 제거, 쿠키에서 Refresh Token도 제거
     *
     * @param refreshToken 삭제할 인증 토큰
     * @throws RefreshTokenException 주어진 Refresh Token이 유효하지 않거나 존재하지 않을 경우 발생
     */
    @PostMapping("/api/logout")
    public ResponseEntity<Void> logOut(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        // Redis에서 REFRESH_TOKEN 삭제
        authService.removeAuthentication(refreshToken);

        // 쿠키에서 REFRESH_TOKEN 삭제
        HttpHeaders headers = cookieUtil.removeRefreshToken();

        return ResponseEntity.noContent().headers(headers).build();
    }
}
