package com.memil.yogimukja.auth.dto;

import com.memil.yogimukja.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 로그인 성공 시 리턴할 DTO
 */
@Getter
@ToString
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String name;
    private boolean isLunchRecommendationEnabled;

    public LoginResponse(String accessToken, User user) {
        this.accessToken = accessToken;
        this.name = user.getName();
        this.isLunchRecommendationEnabled = user.getWebHookUrl() != null && user.getLocation() != null;
    }
}
