package com.memil.yogimukja.user.entity;

import com.memil.yogimukja.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "app_user")
@ToString
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String name;

    // 위치 정보
    private Double latitude; // 위도
    private Double longitude; // 경도

    // 점심 추천 기능 활성화 여부 + Discord Webhook
    private String webHookUrl;

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateDiscordWebhook(String webHookUrl) {
        this.webHookUrl = webHookUrl;
    }
}
