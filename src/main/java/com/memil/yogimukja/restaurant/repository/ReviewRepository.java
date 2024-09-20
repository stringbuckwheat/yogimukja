package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.restaurant.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser_Id(Long userId);

    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double getAverageBy(@Param("restaurantId") Long restaurantId);

    Optional<Review> findByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);
}
