package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.batch.dto.RestaurantOverview;

import java.util.List;

public interface RestaurantQueryRepository {
    List<RestaurantOverview> findAllManagementIdAndApiUpdatedAt();
}
