package com.memil.yogimukja.restaurant.dto;

import com.memil.yogimukja.restaurant.enums.RestaurantSort;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RestaurantQueryParams {
    private Double latitude;
    private Double longitude;
    private Double range;
    private RestaurantSort sort;
    private String search;
    private String filter;

    public RestaurantQueryParams(Double latitude, Double longitude, Double range, String sort, String search, String filter) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.sort = RestaurantSort.from(sort);
        this.search = search;
        this.filter = filter;
    }
}

