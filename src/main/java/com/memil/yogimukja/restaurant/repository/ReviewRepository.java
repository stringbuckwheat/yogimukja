package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.restaurant.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser_Id(Long userId);
}
