package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantQueryRepository restaurantRepository;

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getDetail(Long restaurantId) {
        return restaurantRepository.findDetail(restaurantId);
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
