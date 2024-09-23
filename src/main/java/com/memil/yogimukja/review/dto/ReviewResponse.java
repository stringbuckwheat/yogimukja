package com.memil.yogimukja.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memil.yogimukja.review.model.Review;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Integer rate;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @QueryProjection
    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.rate = review.getRate();
        this.content = review.getContent();
        this.updatedAt = review.getUpdatedDate();
        this.createdAt = review.getCreatedDate();
    }
}
