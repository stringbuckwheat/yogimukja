package com.memil.yogimukja.review.dto;

import com.memil.yogimukja.review.model.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private Long reviewId;
    private Long restaurantId;
    private Integer rate;
    private String content;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.restaurantId = review.getRestaurant().getId();
        this.rate = review.getRate();
        this.content = review.getContent();
        this.updatedAt = review.getUpdatedDate();
        this.createdAt = review.getCreatedDate();
    }
}
