package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @EntityGraph(attributePaths = {"region"})
    Optional<Restaurant> findById(Long id);
}
