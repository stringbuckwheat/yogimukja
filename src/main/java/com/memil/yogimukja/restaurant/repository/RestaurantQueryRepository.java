package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.batch.dto.RestaurantOverview;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantQueryRepository {
    /**
     * 모든 식당의 Management_id(지자체 부여 값)와 API 업데이트 시간을 조회
     * batch에서 INSERT/UPDATE 분리 용도로 사용
     *
     * @return 레스토랑의 관리 ID와 API 업데이트 시간 목록
     */
    List<RestaurantOverview> findAllManagementIdAndApiUpdatedAt();

    /**
     * 맛집 상세 정보
     * @param restaurantId 레스토랑 ID
     * @return 레스토랑의 상세 정보
     */
    RestaurantResponse findDetail(Long restaurantId);

    /**
     * 주어진 쿼리 파라미터에 기반하여 식당 목록 조회
     * 파라미터) 위도, 경도, 범위, 정렬, 검색, 식당 종류(한식, 중식 등), 페이징
     *
     * @param queryParams 레스토랑 조회를 위한 쿼리 파라미터들
     * @return 조건에 맞는 레스토랑 목록
     */
    List<RestaurantResponse> findBy(RestaurantQueryParams queryParams);


    /**
     * 주어진 지역 ID에 해당하는 식당 목록 (페이지네이션, 별점 높은 순)
     *
     * @param regionId 지역 ID
     * @param pageable 페이지 정보
     * @return 해당 지역의 식당 목록
     */
    List<RestaurantResponse> findByRegion(Long regionId, Pageable pageable);
}
