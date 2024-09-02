package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Restaurant findByNameAndAddress(String name, String address);
}
