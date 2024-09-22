package com.memil.yogimukja.restaurant.dto;

import com.memil.yogimukja.restaurant.model.Restaurant;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class RestaurantSummary {
    private Long id;
    private String name;
    private String address;
    private String restaurantType; // 업태 구분명
    private Double rate;
    private Long reviewCount;

    @QueryProjection
    public RestaurantSummary(Restaurant restaurant, Double rate, Long reviewCount) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.restaurantType = restaurant.getType();
        this.rate = rate;
        this.reviewCount = reviewCount;
    }
}
