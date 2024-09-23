package com.memil.yogimukja.user.dto;

import com.memil.yogimukja.user.entity.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserResponse {
    private String username;
    private String name;
    private Double latitude;
    private Double longitude;
    private String webHookUrl;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.latitude = user.getLocation() == null ? null : user.getLocation().getY();
        this.longitude = user.getLocation() == null ? null : user.getLocation().getX();
        this.webHookUrl = user.getWebHookUrl();
    }
}
