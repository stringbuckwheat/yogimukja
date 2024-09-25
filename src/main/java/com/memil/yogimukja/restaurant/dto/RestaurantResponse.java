package com.memil.yogimukja.restaurant.dto;

import com.memil.yogimukja.restaurant.model.Restaurant;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class RestaurantResponse implements Serializable {
    private Long id;
    private String name;
    private String address;
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String type; // 업태 구분명
    private Double rate;
    private Long reviewCount;
    private Integer distance; // 미터 단위

    @QueryProjection
    public RestaurantResponse(Restaurant restaurant, Double rate, Long reviewCount, Double distance) {
        this(restaurant, rate, reviewCount);
        this.distance = distance != null ? distance.intValue() : null;
    }

    @QueryProjection
    public RestaurantResponse(Restaurant restaurant, Double rate, Long reviewCount) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLocation().getY();
        this.longitude = restaurant.getLocation().getX();
        this.type = restaurant.getType();
        this.rate = rate;
        this.reviewCount = reviewCount;
    }
}
