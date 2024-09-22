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
    private Double latitude;
    private Double longitude;
    private Double range;
    private RestaurantSort sort;
    private String search;
    private List<RestaurantType> type;
    private Pageable pageable;

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

