package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.common.error.ErrorMessage;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantQueryRepository restaurantRepository;
    private final RestaurantCacheService restaurantCacheService;

    @Override
    public RestaurantResponse getDetail(Long restaurantId) {
        // 조회수 증가
        restaurantCacheService.increaseViewCount(restaurantId);

        // 캐시에서 데이터 가져오기 (있다면)
        RestaurantResponse cachedRestaurant = restaurantCacheService.fetchFromCache(restaurantId);

        if (cachedRestaurant != null) {
            log.info("return from cache for Id: {}", restaurantId);
            return cachedRestaurant;
        }

        // 데이터베이스에서 가져오기
        RestaurantResponse restaurant = restaurantRepository.findDetail(restaurantId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage()));

        // 캐시 업데이트
        return restaurantCacheService.cacheRestaurantDetail(restaurantId, restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllBy(RestaurantQueryParams queryParams) {
        return restaurantRepository.findBy(queryParams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getByRegion(Long regionId, Pageable pageable) {
        return restaurantRepository.findByRegion(regionId, pageable);
    }
}
