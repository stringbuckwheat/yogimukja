package com.memil.yogimukja.restaurant.controller;

import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {
    private final RestaurantService restaurantService;

    /**
     * 식당 상세 정보 조회
     *
     * @param restaurantId 조회할 식당 ID
     * @return 레스토랑의 상세 정보
     */
    @GetMapping("/api/restaurant/{id}")
    public ResponseEntity<RestaurantResponse> getDetail(@PathVariable(name = "id") Long restaurantId) {
        return ResponseEntity.ok().body(restaurantService.getDetail(restaurantId));
    }

    /**
     * 최근 일주일 간 리뷰가 올라온 식당 리스트 (10개)
     * @return 최근 일주일 간 리뷰가 올라온 식당 상세 정보 리스트
     */
    @GetMapping("/api/restaurant/popular")
    public ResponseEntity<List<RestaurantResponse>> getPopular() {
        LocalDateTime startDate = LocalDate.now().minusDays(7).atStartOfDay();
        return ResponseEntity.ok().body(restaurantService.getPopular(startDate));
    }

    /**
     * 주어진 쿼리 파라미터에 따라 식당 목록 조회
     *
     * @param latitude   검색할 레스토랑의 위도 (선택 사항)
     * @param longitude  검색할 레스토랑의 경도 (선택 사항)
     * @param range      검색 반경 (선택 사항)
     * @param sort       정렬 기준 (기본값: 거리)
     * @param search     검색어 (선택 사항)
     * @param type       레스토랑 유형 리스트 (선택 사항)
     * @param pageable   페이지 정보
     * @return 조건에 맞는 레스토랑 목록이 포함된 ResponseEntity
     */
    @GetMapping("/api/restaurant")
    public ResponseEntity<List<RestaurantResponse>> getAllBy(@RequestParam(name = "lat", required = false) Double latitude,
                                                            @RequestParam(name = "lon", required = false) Double longitude,
                                                            @RequestParam(name = "range", required = false) Double range,
                                                            @RequestParam(name = "sort", defaultValue = "distance", required = false) String sort,
                                                            @RequestParam(name = "search", required = false) String search,
                                                            @RequestParam(name = "type", required = false) List<String> type,
                                                            @PageableDefault(page = 0, size = 10) Pageable pageable

    ) {
        RestaurantQueryParams queryParams = new RestaurantQueryParams(latitude, longitude, range, sort, search, type, pageable);

        return ResponseEntity.ok().body(restaurantService.getAllBy(queryParams));
    }

    /**
     * 주어진 지역 ID에 따라 식당 목록을 조회
     * '구' 별 조회
     *
     * @param regionId 조회할 지역의 ID
     * @param pageable 페이지 정보
     * @return 해당 지역의 식당 목록
     */
    @GetMapping("/api/restaurant/region/{id}")
    public ResponseEntity<List<RestaurantResponse>> getAllByRegion(@PathVariable(name = "id") Long regionId,
                                                                   @PageableDefault(page = 0, size = 10) Pageable pageable
                                                                   ) {
        return ResponseEntity.ok().body(restaurantService.getByRegion(regionId, pageable));
    }
}
