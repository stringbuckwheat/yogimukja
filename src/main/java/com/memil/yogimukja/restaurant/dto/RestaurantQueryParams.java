package com.memil.yogimukja.restaurant.dto;

import com.memil.yogimukja.restaurant.enums.RestaurantSort;
import com.memil.yogimukja.restaurant.enums.RestaurantType;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantQueryParams {
    private Double latitude; // 위도
    private Double longitude; // 경도
    private Double range; // 범위
    private RestaurantSort sort; // 정렬
    private String search; // 검색어
    private List<RestaurantType> type; // 업태 구분 명(한식, 중식 ...)
    private Pageable pageable; // 페이징

    public RestaurantQueryParams(Double latitude, Double longitude, Double range, String sort, String search, List<String> type, Pageable pageable) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.sort = RestaurantSort.from(sort);
        this.search = search;
        this.type = type == null ? null : type.stream().map(RestaurantType::getInstance).toList();
        this.pageable = pageable;
    }
}

