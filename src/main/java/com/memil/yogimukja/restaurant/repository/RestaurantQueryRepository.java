package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.batch.dto.RestaurantOverview;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantSummary;

import java.util.List;

public interface RestaurantQueryRepository {
    List<RestaurantOverview> findAllManagementIdAndApiUpdatedAt();
    List<RestaurantSummary> findRestaurants(RestaurantQueryParams queryParams);
}
