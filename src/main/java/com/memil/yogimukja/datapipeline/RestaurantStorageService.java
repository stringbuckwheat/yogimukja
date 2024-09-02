package com.memil.yogimukja.datapipeline;

import com.memil.yogimukja.restaurant.entity.Restaurant;
import com.memil.yogimukja.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantStorageService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public void saveOrUpdateRestaurants(List<Restaurant> restaurants) {
        // 기존 레스토랑을 조회할 때 사용할 맵 생성
        Map<String, Restaurant> existingRestaurants = new HashMap<>();

        // 기존 레스토랑을 데이터베이스에서 한 번에 조회
        for (Restaurant restaurant : restaurants) {
            String key = generateUniqueKey(restaurant.getName(), restaurant.getAddress());
            if (!existingRestaurants.containsKey(key)) {
                Restaurant existingRestaurant = restaurantRepository.findByNameAndAddress(restaurant.getName(), restaurant.getAddress());
                if (existingRestaurant != null) {
                    existingRestaurants.put(key, existingRestaurant);
                }
            }
        }

        // 새로운 레스토랑과 업데이트가 필요한 레스토랑 분리
        List<Restaurant> toSave = new ArrayList<>();
        List<Restaurant> toUpdate = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            String key = generateUniqueKey(restaurant.getName(), restaurant.getAddress());
            if (existingRestaurants.containsKey(key)) {
                Restaurant existingRestaurant = existingRestaurants.get(key);
                existingRestaurant.update(restaurant);
                toUpdate.add(existingRestaurant);
            } else {
                toSave.add(restaurant);
            }
        }

        // 배치 저장
        if (!toSave.isEmpty()) {
            restaurantRepository.saveAll(toSave);
        }
        if (!toUpdate.isEmpty()) {
            restaurantRepository.saveAll(toUpdate); // saveAll은 새 객체와 기존 객체 모두를 처리함
        }
    }

    private String generateUniqueKey(String name, String address) {
        // 유니크 키를 생성하여 레스토랑을 식별
        return name + "::" + address;
    }
}
