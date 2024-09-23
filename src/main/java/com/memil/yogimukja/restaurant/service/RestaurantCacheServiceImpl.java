package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.model.RestaurantView;
import com.memil.yogimukja.restaurant.repository.RestaurantViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantCacheServiceImpl implements RestaurantCacheService {
    private final RestaurantViewRepository restaurantViewRepository; // Redis 조회수 저장 리파지토리
    private final CacheManager cacheManager;
    private static final int THRESHOLD = 10; // 캐시 기준 조회수

    @Override
    public void increaseViewCount(Long restaurantId) {
        // Redis에서 조회수 가져오기
        RestaurantView restaurantView = restaurantViewRepository.findById(restaurantId)
                .orElse(new RestaurantView(restaurantId, 0));

        restaurantView.increaseViewCount(); // 조회수 증가
        restaurantViewRepository.save(restaurantView); // Redis에 저장
    }

    @Override
    public RestaurantResponse fetchFromCache(Long restaurantId) {
        Cache cache = cacheManager.getCache("restaurantDetails");

        if (cache != null) {
            var cachedValue = cache.get(restaurantId);
            return (cachedValue != null) ? (RestaurantResponse) cachedValue.get() : null;
        }

        return null;
    }

    @Override
    public RestaurantResponse cacheRestaurantDetail(Long restaurantId, RestaurantResponse restaurant) {
        // 최근 조회수가 높은 레스토랑만 캐시 업데이트
        if (shouldCache(restaurantId)) {
            log.info("조회수가 높은 레스토랑 캐싱 - ID: {}", restaurantId);

            Cache cache = cacheManager.getCache("restaurantDetails");
            if (cache != null) {
                cache.put(restaurantId, restaurant);  // 캐시에 수동으로 값 넣기
            }

            return restaurant; // 캐시된 값을 반환
        }

        log.info("캐싱 안함 - ID: {}", restaurantId);
        return restaurant;
    }

    private boolean shouldCache(Long restaurantId) {
        // Redis에서 조회수 가져오기
        RestaurantView restaurantView = restaurantViewRepository.findById(restaurantId).orElse(new RestaurantView());
        int viewCount = restaurantView.getViewCount(); // 조회수 가져오기
        return viewCount > THRESHOLD; // THRESHOLD를 기준으로 캐시 여부 결정
    }
}
