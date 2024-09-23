package com.memil.yogimukja.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequest {
    @NotNull(message = "점수를 입력해주세요")
    @Min(value = 1, message = "점수는 1 이상이어야 합니다.")
    @Max(value = 5, message = "점수는 5 이하여야 합니다.")
    private Integer rate; // 점수

    private String content; // 내용 (빈 칸 가능)
}
