package com.memil.yogimukja.user.dto;

import com.memil.yogimukja.common.validation.ValidDiscordWebhook;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LunchRequest {
    @NotBlank
    @ValidDiscordWebhook
    private String webHookUrl;
    private Double latitude; // 위도
    private Double longitude; // 경도
}
