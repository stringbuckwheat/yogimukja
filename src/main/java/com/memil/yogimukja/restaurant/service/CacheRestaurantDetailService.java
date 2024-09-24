package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.restaurant.dto.RestaurantResponse;

import java.util.Optional;

public interface CacheRestaurantDetailService {
    /**
     * 주어진 레스토랑의 조회수 증가 (조회수는 Redis에 저장)
     *
     * @param restaurantId 레스토랑 ID
     */
    void increaseViewCount(Long restaurantId);

    /**
     * 캐시에서 레스토랑 상세 정보 조회
     *
     * @param restaurantId 레스토랑 ID
     * @return 캐시에서 조회한 레스토랑 상세 정보가 포함된 Optional.
     *         존재하지 않을 경우 Optional.empty()
     */
    Optional<RestaurantResponse> fetchFromCache(Long restaurantId);

    /**
     * 한 시간 내에 10번 이상 조회되었을 시, 레스토랑 상세 정보 캐시에 저장
     *
     * @param restaurantId 레스토랑 ID
     * @param restaurant 레스토랑 상세 정보 DTO
     * @return 캐시에 저장된 레스토랑 상세 정보
     */
    RestaurantResponse cacheRestaurantDetail(Long restaurantId, RestaurantResponse restaurant);
}
