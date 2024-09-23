package com.memil.yogimukja.review.dto;

import com.memil.yogimukja.review.model.Review;
import lombok.Getter;

@Getter
public class ReviewDetail extends ReviewResponse {
    private Long restaurantId;
    private String restaurantName;
    private String address;

    public ReviewDetail(Review review) {
        super(review);

        this.restaurantId = review.getRestaurant().getId();
        this.restaurantName = review.getRestaurant().getName();
        this.address = review.getRestaurant().getAddress();
    }
}
