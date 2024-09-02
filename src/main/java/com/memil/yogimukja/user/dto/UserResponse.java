package com.memil.yogimukja.user.dto;

import com.memil.yogimukja.user.entity.User;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class UserResponse {
    private String username;
    private String name;
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.createdAt = user.getCreatedDate();
    }
}
