package com.memil.yogimukja.restaurant.controller;

import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.dto.RestaurantSummary;
import com.memil.yogimukja.restaurant.service.RestaurantServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {
    private final RestaurantServiceImpl restaurantService;

    @GetMapping("/api/restaurant/{id}")
    public ResponseEntity<RestaurantResponse> getDetail(@PathVariable(name = "id") Long restaurantId) {
        return ResponseEntity.ok().body(restaurantService.getDetail(restaurantId));
    }

    @GetMapping("/api/restaurant")
    public ResponseEntity<List<RestaurantSummary>> getAllBy(@RequestParam(name = "lat") Double latitude,
                                                            @RequestParam(name = "lon") Double longitude,
                                                            @RequestParam(name = "range") Double range,
                                                            @RequestParam(name = "sort", defaultValue = "distance", required = false) String sort,
                                                            @RequestParam(name = "search", required = false) String search,
                                                            @RequestParam(name = "filter", required = false) String filter
                                                            // TODO 페이징
                                                             ) {
        RestaurantQueryParams queryParams = new RestaurantQueryParams(latitude, longitude, range, sort, search, filter);

        return ResponseEntity.ok().body(restaurantService.getAllBy(queryParams));
    }
}
