package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.common.error.ErrorMessage;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.dto.RestaurantSummary;
import com.memil.yogimukja.restaurant.entity.Restaurant;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import com.memil.yogimukja.restaurant.repository.RestaurantRepository;
import com.memil.yogimukja.restaurant.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantQueryRepository restaurantQueryRepository;
    private final ReviewRepository reviewRepository;

    public RestaurantResponse getDetail(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage()));

        // 평점
        Double average = reviewRepository.getAverageBy(restaurantId);

        // TODO 리뷰들, 내가 쓴 리뷰 등

        return new RestaurantResponse(restaurant, average);
    }

    public List<RestaurantSummary> getAllBy(RestaurantQueryParams queryParams) {
        return restaurantQueryRepository.findRestaurantsWithinDistance(queryParams);
    }
}
