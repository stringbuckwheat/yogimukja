package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl {
    private final RestaurantRepository restaurantRepository;

    // 맛집 (추가 필드 관리)
    // 평점: Double (초기값 0.0, 모든 평가의 평균)

    // 맛집 목록
    // GIS
    // 파라미터 (lat: y축, lon: x축, range: double, 정렬 기능: 거리 순, 평점 순, 이름 순 등)

    // 맛집 상세 정보
    // 맛집의 모든 피드 + 평점 + 리뷰

}
