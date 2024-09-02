package com.memil.yogimukja.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 로그인 성공 시 리턴할 DTO
 */
@AllArgsConstructor
@Getter
@ToString
public class LoginResponse {
    private String username;
    private String accessToken;
}
