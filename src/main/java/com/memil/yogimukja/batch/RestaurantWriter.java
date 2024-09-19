package com.memil.yogimukja.batch;

import com.memil.yogimukja.restaurant.entity.Restaurant;
import com.memil.yogimukja.restaurant.repository.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantWriter implements ItemWriter<List<Restaurant>> {

    private final RestaurantRepository restaurantRepository;

    private Map<String, Restaurant> existingRestaurantMap;

    @PostConstruct
    public void init() {
        log.info(">>>>>>>>> Initializing Restaurant Writer");

        // 초기 로드: 데이터베이스에서 모든 레스토랑을 조회하여 캐시
        List<Restaurant> existingRestaurants = restaurantRepository.findAll();
        existingRestaurantMap = existingRestaurants.stream()
                .collect(Collectors.toMap(
                        r -> generateUniqueKey(r.getName(), r.getAddress()),
                        r -> r,
                        (existing, replacement) -> {
                            // 키 중복 시 업데이트 된 날짜가 최근인 엔티티 선택
                            return existing.getApiUpdatedAt().isAfter(replacement.getApiUpdatedAt()) ? existing : replacement;
                        }
                ));
    }

    private String generateUniqueKey(String name, String address) {
        return name + "::" + address;
    }

    @Override
    public void write(Chunk<? extends List<Restaurant>> chunk) throws Exception {
        // chunk -> list
        List<Restaurant> restaurants = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        // 새로운 레스토랑과 업데이트가 필요한 레스토랑 분리
        List<Restaurant> toSave = new ArrayList<>();
        List<Restaurant> toUpdate = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            String key = generateUniqueKey(restaurant.getName(), restaurant.getAddress());

            if (existingRestaurantMap.containsKey(key)) {
                Restaurant existingRestaurant = existingRestaurantMap.get(key);
                existingRestaurant.update(restaurant);
                toUpdate.add(existingRestaurant);
            } else {
                toSave.add(restaurant);
            }
        }

        // 배치 저장
        if (!toSave.isEmpty()) {
            restaurantRepository.saveAll(toSave);
            // 새로 저장된 레스토랑을 map에 추가
            for (Restaurant restaurant : toSave) {
                String key = generateUniqueKey(restaurant.getName(), restaurant.getAddress());
                existingRestaurantMap.put(key, restaurant);
            }
        }

        if (!toUpdate.isEmpty()) {
            restaurantRepository.saveAll(toUpdate);
            // 업데이트된 레스토랑을 map에 반영
            for (Restaurant restaurant : toUpdate) {
                String key = generateUniqueKey(restaurant.getName(), restaurant.getAddress());
                existingRestaurantMap.put(key, restaurant);
            }
        }
    }
}