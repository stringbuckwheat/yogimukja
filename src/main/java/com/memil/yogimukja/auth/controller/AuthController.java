package com.memil.yogimukja.auth.controller;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginRequest;
import com.memil.yogimukja.auth.dto.LoginResponse;
import com.memil.yogimukja.auth.service.AuthService;
import com.memil.yogimukja.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    /**
     * 로그인 엔드포인트
     *
     * @param loginRequest username, password
     * @return Access Token, Refresh token
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
     * @return 새로운 Access Token
     */
    @PostMapping("/api/token")
    public ResponseEntity<String> reissueToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken){
        return ResponseEntity.ok().body(authService.reissueToken(refreshToken));
    }

    /**
     * 로그아웃
     * Redis/쿠키에서 Refresh Token 삭제
     *
     * @param refreshToken
     * @return
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
