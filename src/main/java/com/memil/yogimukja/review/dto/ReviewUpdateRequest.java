package com.memil.yogimukja.review.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReviewUpdateRequest {
    private ReviewRequest reviewRequest;
    private Long reviewId;
    private Long restaurantId;
    private Long userId;
}