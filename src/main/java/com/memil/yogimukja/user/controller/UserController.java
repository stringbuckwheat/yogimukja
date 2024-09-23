package com.memil.yogimukja.user.controller;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.auth.dto.LoginResponse;
import com.memil.yogimukja.auth.model.UserCustom;
import com.memil.yogimukja.common.error.exception.HasSameUsernameException;
import com.memil.yogimukja.common.util.CookieUtil;
import com.memil.yogimukja.user.dto.LunchRequest;
import com.memil.yogimukja.user.dto.UserRequest;
import com.memil.yogimukja.user.dto.UserResponse;
import com.memil.yogimukja.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final CookieUtil cookieUtil;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    /**
     * 아이디 중복 확인
     *
     * @param username 아이디
     * @return username 유효한 아이디
     * @throws HasSameUsernameException 아이디 중복 시
     */
    @GetMapping("/api/user/username")
    public ResponseEntity<String> hasSameUsername(@RequestBody String username) {
        return ResponseEntity.ok().body(userService.hasSameUsername(username));
    }

    /**
     * 회원 가입
     *
     * @param userRequest 회원가입 요청 객체
     * @return Access Token, Refresh Token(Cookie), 회원 이름
     */
    @PostMapping("/api/user")
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid UserRequest userRequest) {
        AuthTokens authTokens = userService.register(userRequest);

        // 쿠키에 Refresh token 담기
        HttpHeaders headers = cookieUtil.getHeaderWithRefreshToken(authTokens.getRefreshToken(), 4 * 60 * 60);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(authTokens.getLoginResponse());
    }

    /**
     * 사용자의 점심 추천 상태 업데이트
     *
     * @param lunchRequest 위도, 경도, 디스코드 Webhook
     * @param userCustom 인증된 사용자 객체
     * @return void, 204
     * @throws UsernameNotFoundException 주어진 ID의 사용자가 존재하지 않을 경우
     */
    @PutMapping("/api/user/lunch")
    public ResponseEntity<Void> updateLunchRecommendationStatus(@RequestBody @Valid LunchRequest lunchRequest, @AuthenticationPrincipal UserCustom userCustom) {
        userService.updateLunchRecommendationStatus(lunchRequest, userCustom.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 상세 정보
     *
     * @param userCustom 인증된 사용자 객체
     * @return 사용자 상세 정보를 포함하는 응답 객체
     * @throws UsernameNotFoundException 주어진 ID의 사용자를 찾을 수 없는 경우
     */
    @GetMapping("/api/user")
    public ResponseEntity<UserResponse> getDetail(@AuthenticationPrincipal UserCustom userCustom) {
        UserResponse response = userService.getDetail(userCustom.getId());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 사용자 정보 수정
     *
     * @param userRequest 업데이트할 사용자 정보를 포함한 요청 객체
     * @param userCustom 인증된 사용자 객체
     * @return 업데이트된 사용자 정보를 포함한 응답 객체
     * @throws UsernameNotFoundException 주어진 ID의 사용자를 찾을 수 없는 경우
     */
    @PutMapping("/api/user")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest userRequest, @AuthenticationPrincipal UserCustom userCustom) {
        UserResponse response = userService.update(userRequest, userCustom.getId());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 회원 탈퇴 (리프레쉬 토큰 무효화)
     *
     * @param userCustom 인증된 사용자 객체
     * @param refreshToken 무효화할 리프레쉬 토큰
     * @return Void, 204
     */
    @DeleteMapping("/api/user")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserCustom userCustom,
                                       @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        userService.delete(userCustom.getId(), refreshToken);

        // 쿠키에서 REFRESH_TOKEN 삭제
        HttpHeaders headers = cookieUtil.removeRefreshToken();

        return ResponseEntity.noContent().headers(headers).build();
    }
}
