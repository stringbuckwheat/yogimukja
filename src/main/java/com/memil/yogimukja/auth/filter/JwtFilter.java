package com.memil.yogimukja.auth.filter;

import com.memil.yogimukja.auth.service.TokenProvider;
import com.memil.yogimukja.auth.token.AccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 유효성 검사 필터
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    /**
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ExpiredJwtException Access Token 유효시간 만료 시
     * @throws JwtException MalformedJwtException 등 유효하지 않은 JWT가 들어왔을 떄
     * @throws NumberFormatException Jwt Claim 내부 정보 형변환 과정에서 발생 가능
     *
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("*** JWT FILTER: REQUEST URL: {}", request.getRequestURL());

        // 헤더에서 Access Token 추출
        String token = resolveToken(request);

        if (token != null) {
            // JWT -> Access Token 객체
            AccessToken accessToken = tokenProvider.convertAccessToken(token);

            // SecurityContext에 등록
            setAuthenticationFromClaims(accessToken.getData());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 헤더에서 token 추출
     *
     * @param request
     * @return Authorization 헤더에 Bearer로 시작하는 토큰이 포함되어 왔을 때, 'Bearer' 떼고 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }

        return null;
    }

    private void setAuthenticationFromClaims(Claims claims) {
        Long userId = Long.valueOf(String.valueOf(claims.get("aud")));
        String username = claims.getSubject();

        tokenProvider.setAuthentication(userId, username);
    }
}
