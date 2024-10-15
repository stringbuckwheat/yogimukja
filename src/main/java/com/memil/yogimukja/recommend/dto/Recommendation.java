package com.memil.yogimukja.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Recommendation {
    private String webHookUrl;
    private String message;
}
