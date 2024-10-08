package com.memil.yogimukja.restaurant.service;

import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.dto.UserLocation;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface RestaurantService {
    /**
     * 맛집 상세 정보
     *
     * @param restaurantId 레스토랑 ID
     * @return 레스토랑의 상세 정보
     */
    RestaurantResponse getDetail(Long restaurantId);

    /**
     * 해당 날짜 이후 오늘까지 리뷰가 등록된 식당을 평점순으로 정렬하여 반환
     * 결과는 캐싱
     *
     * @param startDate    필터링 시작일
     * @param userLocation 현재 사용자의 위치(위도, 경도)
     * @return 조건에 맞는 식당 리스트
     */
    List<RestaurantResponse> getPopular(LocalDateTime startDate, UserLocation userLocation);

    /**
     * 주어진 쿼리 파라미터에 기반하여 식당 목록 조회
     * 파라미터) 위도, 경도, 범위, 정렬, 검색, 식당 종류(한식, 중식 등), 페이징
     *
     * @param queryParams 레스토랑 조회를 위한 쿼리 파라미터들
     * @return 조건에 맞는 레스토랑 목록
     */
    @Transactional(readOnly = true)
    List<RestaurantResponse> getAllBy(RestaurantQueryParams queryParams);

    /**
     * 주어진 지역 ID에 해당하는 식당 목록 (페이지네이션, 별점 높은 순)
     *
     * @param regionId     지역 ID
     * @param pageable     페이지 정보
     * @param userLocation 현재 사용자의 위치(위도, 경도)
     * @return 해당 지역의 식당 목록
     */
    @Transactional(readOnly = true)
    List<RestaurantResponse> getByRegion(Long regionId, Pageable pageable, UserLocation userLocation);
}
