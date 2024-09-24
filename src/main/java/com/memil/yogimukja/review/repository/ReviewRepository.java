package com.memil.yogimukja.review.repository;

import com.memil.yogimukja.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"restaurant"})
    List<Review> findByUser_Id(Long userId, Pageable pageable);

    Optional<Review> findByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);

    Page<Review> findByRestaurant_id(Long restaurantId, Pageable pageable);

    List<Review> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
