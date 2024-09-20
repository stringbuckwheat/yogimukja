package com.memil.yogimukja.restaurant.dto;

import com.memil.yogimukja.restaurant.entity.Restaurant;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String restaurantType; // 업태 구분명

    private String region; // 구 정보
    private Double rate;

    // TODO reviews

    @QueryProjection
    public RestaurantResponse(Restaurant restaurant, Double rate) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLocation().getY();
        this.longitude = restaurant.getLocation().getX();
        this.restaurantType = restaurant.getRestaurantType();
        this.region = restaurant.getRegion().getName();
        this.rate = rate;
    }

    public RestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLocation().getY();
        this.longitude = restaurant.getLocation().getX();
        this.restaurantType = restaurant.getRestaurantType();

        if(restaurant.getRegion() != null) {
            this.region = restaurant.getRegion().getName();
        }
    }
}
