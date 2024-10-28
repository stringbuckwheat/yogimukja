package com.memil.yogimukja.user.dto;

import com.memil.yogimukja.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserRequest {
    @NotBlank(message = "아이디는 비워둘 수 없습니다.")
    @Size(min = 3, max = 20, message = "아이디는 3자 이상 20자 이하여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=()]).*$",
            message = "비밀번호는 숫자, 문자, 특수문자(!@#$%^&*+=())를 모두 포함해야합니다.")
    private String password;
    @NotBlank(message = "이름은 비워둘 수 없습니다.")
    private String name;

    public void encryptPassword(String encryptPassword) {
        this.password = encryptPassword;
    }

    public User toEntity() {
        return new User(username, password, name);
    }
}
