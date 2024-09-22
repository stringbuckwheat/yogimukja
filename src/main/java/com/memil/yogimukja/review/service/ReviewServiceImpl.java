package com.memil.yogimukja.review.service;

import com.memil.yogimukja.common.error.ErrorMessage;
import com.memil.yogimukja.review.dto.ReviewRequest;
import com.memil.yogimukja.review.dto.ReviewResponse;
import com.memil.yogimukja.review.dto.ReviewUpdateRequest;
import com.memil.yogimukja.restaurant.model.Restaurant;
import com.memil.yogimukja.review.model.Review;
import com.memil.yogimukja.restaurant.repository.RestaurantRepository;
import com.memil.yogimukja.review.repository.ReviewRepository;
import com.memil.yogimukja.user.entity.User;
import com.memil.yogimukja.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyAllReview(Long userId) {
        return reviewRepository.findByUser_Id(userId).stream().map(ReviewResponse::new).toList();
    }

    // 맛집 평가
    @Transactional
    public ReviewResponse add(ReviewRequest reviewRequest, Long restaurantId, Long userId) {
        Optional<Review> existingReview = reviewRepository.findByUser_IdAndRestaurant_Id(userId, restaurantId);

        if(existingReview.isPresent()) {
            throw new IllegalStateException(ErrorMessage.ALREADY_RATED_RESTAURANT.getMessage());
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        Review review = reviewRepository.save(
                Review.builder()
                        .restaurant(restaurant)
                        .user(user)
                        .rate(reviewRequest.getRate())
                        .content(reviewRequest.getContent())
                        .build()
        );

        return new ReviewResponse(review);
    }

    // 맛집 평가 수정
    @Transactional
    public ReviewResponse update(ReviewUpdateRequest request) {
        Review review = getReview(request.getReviewId(), request.getUserId());

        Restaurant restaurant = review.getRestaurant();

        // 변경 시 Restaurant 조회
        if(!restaurant.getId().equals(request.getRestaurantId())) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new NoSuchElementException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage()));
        }

        // 상태 업데이트
        review.update(restaurant, request.getReviewRequest().getRate(), request.getReviewRequest().getContent());

        return new ReviewResponse(review);
    }

    // 맛집 평가 삭제
    @Transactional
    public void delete(Long reviewId, Long userId) {
        Review review = getReview(reviewId, userId);
        reviewRepository.delete(review);
    }

    private Review getReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.REVIEW_NOT_FOUND.getMessage()));

        // 권한 검사
        if(!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorMessage.NOT_YOUR_REVIEW.getMessage());
        }

        return review;
    }
}
