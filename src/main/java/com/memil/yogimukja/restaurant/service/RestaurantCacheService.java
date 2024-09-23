package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.restaurant.dto.RestaurantResponse;

public interface RestaurantCacheService {
    void increaseViewCount(Long restaurantId);

    RestaurantResponse fetchFromCache(Long restaurantId);

    RestaurantResponse cacheRestaurantDetail(Long restaurantId, RestaurantResponse restaurant);
}
