package com.memil.yogimukja.common.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    public HttpHeaders removeRefreshToken() {
        return getHeaderWithRefreshToken("", 0);
    }

    public HttpHeaders getHeaderWithRefreshToken(String refreshToken, long maxAge) {
        // authTokens.getRefreshToken()
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken, maxAge);

        // HTTP 응답 헤더에 쿠키 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private ResponseCookie createRefreshTokenCookie(String value, long maxAge) {
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, value, maxAge);
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)  // JavaScript에서 쿠키에 접근하지 못하게 설정
                .path("/")       // 쿠키의 경로 설정
                .secure(false) // HTTP 요청 허락
                .maxAge(maxAge) // 유효시간
                .sameSite("Strict") // SameSite 설정
                .build();
    }
}
