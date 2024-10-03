package com.memil.yogimukja.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserUpdate {
    @NotBlank(message = "이름은 비워둘 수 없습니다.")
    private String name;
}
