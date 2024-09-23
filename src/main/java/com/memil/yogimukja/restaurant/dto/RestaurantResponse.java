package com.memil.yogimukja.restaurant.dto;

import com.memil.yogimukja.restaurant.model.Restaurant;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String type; // 업태 구분명

    private String region; // 구 정보
    private Double rate;
    private Long reviewCount;

    @QueryProjection
    public RestaurantResponse(Restaurant restaurant, Double rate, Long reviewCount) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLocation().getY();
        this.longitude = restaurant.getLocation().getX();
        this.type = restaurant.getType();
        this.region = restaurant.getRegion().getName();
        this.rate = rate;
        this.reviewCount = reviewCount;
    }
}
