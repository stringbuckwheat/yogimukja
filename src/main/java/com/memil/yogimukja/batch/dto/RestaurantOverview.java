package com.memil.yogimukja.batch.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RestaurantOverview {
    private String managementId;
    private LocalDateTime apiUpdatedAt;

    @QueryProjection
    public RestaurantOverview(String managementId, LocalDateTime apiUpdatedAt) {
        this.managementId = managementId;
        this.apiUpdatedAt = apiUpdatedAt;
    }

    public RestaurantOverview(RestaurantPayload restaurant) {
        this.managementId = restaurant.getManagementId();
        this.apiUpdatedAt = restaurant.getApiUpdatedAt();
    }
}
