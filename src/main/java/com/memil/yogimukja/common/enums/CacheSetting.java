package com.memil.yogimukja.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@AllArgsConstructor
@Getter
public enum CacheSetting {
    REVIEW_TOP_RESTAURANT("reviewTopRestaurant", Duration.ofDays(1L)),
    RESTAURANT_DETAILS("restaurantDetails", Duration.ofHours(1));

    private String cacheName;
    private Duration ttl;
}
