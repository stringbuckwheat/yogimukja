package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.common.error.ErrorMessage;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantQueryRepository restaurantQueryRepository;
    private final CacheRestaurantDetailService cacheRestaurantDetailService;

    @Override
    public RestaurantResponse getDetail(Long restaurantId) {
        // 조회수 증가
        cacheRestaurantDetailService.increaseViewCount(restaurantId);

        // 캐시에서 데이터 가져오기 (있다면)
        Optional<RestaurantResponse> cachedRestaurant = cacheRestaurantDetailService.fetchFromCache(restaurantId);

        if (cachedRestaurant.isPresent()) {
            log.info("캐싱된 결과 반환 - Id: {}", restaurantId);
            return cachedRestaurant.get();
        }

        // 데이터베이스에서 가져오기
        RestaurantResponse restaurant = restaurantQueryRepository.findDetail(restaurantId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage()));

        // 캐시 업데이트
        return cacheRestaurantDetailService.cacheRestaurantDetail(restaurantId, restaurant);
    }

    @Cacheable(cacheNames = "reviewTopRestaurant", key = "#p0")
    public List<RestaurantResponse> getPopular(LocalDateTime startDate) {
        // 최근 리뷰 점수가 높은 식당들 10개 반환
        return restaurantQueryRepository.findPopular(startDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllBy(RestaurantQueryParams queryParams) {
        return restaurantQueryRepository.findBy(queryParams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getByRegion(Long regionId, Pageable pageable) {
        return restaurantQueryRepository.findByRegion(regionId, pageable);
    }
}
