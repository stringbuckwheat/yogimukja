package com.memil.yogimukja.user.dto;

import com.memil.yogimukja.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserRequest {
    private String username;// 유효성 검증
    private String password; // 유효성 검증
    @NotNull
    private String name;

    public void encryptPassword(String encryptPassword) {
        this.password = encryptPassword;
    }

    public User toEntity() {
        return new User(username, password, name);
    }
}
