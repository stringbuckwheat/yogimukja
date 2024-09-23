package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.restaurant.model.RestaurantView;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RestaurantViewRepository extends Repository<RestaurantView, Long> {
    Optional<RestaurantView> findById(Long restaurantId);
    void save(RestaurantView restaurantView);
}
