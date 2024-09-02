package com.memil.yogimukja.user.controller;

import com.memil.yogimukja.auth.model.UserCustom;
import com.memil.yogimukja.common.error.exception.HasSameUsernameException;
import com.memil.yogimukja.common.util.CookieUtil;
import com.memil.yogimukja.user.dto.LocationRequest;
import com.memil.yogimukja.user.dto.UserRequest;
import com.memil.yogimukja.user.dto.UserResponse;
import com.memil.yogimukja.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * @param username id
     * @return username
     * @throws HasSameUsernameException 아이디 중복 시
     */
    @GetMapping("/api/user/username")
    public ResponseEntity<String> hasSameUsername(@PathVariable(name = "username") String username) {
        return ResponseEntity.ok().body(userService.hasSameUsername(username));
    }

    /**
     * 회원 가입
     *
     * @param userRequest
     * @return
     */
    @PostMapping("/api/user/register")
    public ResponseEntity<Void> register(@RequestBody UserRequest userRequest) {
        userService.register(userRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/user/location")
    public ResponseEntity<LocationRequest> updateLocation(@RequestBody LocationRequest locationRequest, @AuthenticationPrincipal UserCustom userCustom) {
        userService.updateLocation(locationRequest, userCustom.getId());
        return ResponseEntity.ok().body(locationRequest);
    }

    @PutMapping("/api/user/lunch")
    public ResponseEntity<Void> updateLunchRecommendationStatus(@AuthenticationPrincipal UserCustom userCustom) {
        userService.updateLunchRecommendationStatus(userCustom.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/user")
    public ResponseEntity<UserResponse> getDetail(@AuthenticationPrincipal UserCustom userCustom) {
        UserResponse response = userService.getDetail(userCustom.getId());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/api/user")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest userRequest, @AuthenticationPrincipal UserCustom userCustom) {
        UserResponse response = userService.update(userRequest, userCustom.getId());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/api/user")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserCustom userCustom,
                                       @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        userService.delete(userCustom.getId(), refreshToken);

        // 쿠키에서 REFRESH_TOKEN 삭제
        HttpHeaders headers = cookieUtil.removeRefreshToken();

        return ResponseEntity.noContent().headers(headers).build();
    }
}
