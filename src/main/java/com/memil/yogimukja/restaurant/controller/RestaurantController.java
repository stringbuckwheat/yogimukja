package com.memil.yogimukja.restaurant.controller;

import com.memil.yogimukja.datapipeline.RestaurantCollectionService;
import com.memil.yogimukja.datapipeline.RestaurantStorageService;
import com.memil.yogimukja.restaurant.entity.Restaurant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
public class RestaurantController {

    @Autowired
    private RestaurantCollectionService restaurantCollectionService;

    @Autowired
    private RestaurantStorageService restaurantStorageService;

    @GetMapping("/api/restaurant/test")
    public Mono<String> openApiTest() {
        int end = 10000;
        int step = 1000;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<String> apiUrls = new ArrayList<>();
        for (int i = 1; i <= end; i += step) {
            int rangeEnd = Math.min(i + step - 1, end);
            String apiUrl = "/" + i + "/" + rangeEnd;
            apiUrls.add(apiUrl);
        }

        return Flux.fromIterable(apiUrls)
                .flatMap(this::collectAndSaveData)
                .then(Mono.fromCallable(() -> {
                    stopWatch.stop();
                    System.out.println(stopWatch.prettyPrint());
                    System.out.println("코드 실행 시간 (s): " + stopWatch.getTotalTimeSeconds());
                    return "Completed";
                }));
    }

    private Mono<Void> collectAndSaveData(String apiUrl) {
        return restaurantCollectionService.collectDataFromAPI(apiUrl)
                .flatMap(restaurants -> {
                    if (restaurants != null) {
                        return Mono.fromRunnable(() -> restaurantStorageService.saveOrUpdateRestaurants(restaurants));
                    } else {
                        return Mono.empty();
                    }
                });
    }
}


