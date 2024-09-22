package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.RestaurantOverview;
import com.memil.yogimukja.batch.dto.RestaurantPayload;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Lazy
public class RestaurantWriter implements ItemWriter<List<RestaurantPayload>> {

    private final RestaurantQueryRepository restaurantQueryRepository;
    private final JdbcTemplate jdbcTemplate;
    private Map<String, RestaurantOverview> existingRestaurantMap;

    @PostConstruct
    public void init() {
        existingRestaurantMap = restaurantQueryRepository.findAllManagementIdAndApiUpdatedAt().stream()
                .collect(Collectors.toMap(RestaurantOverview::getManagementId, r -> r));
    }

    @Override
    public void write(Chunk<? extends List<RestaurantPayload>> chunk) {
        List<RestaurantPayload> restaurants = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        // 신규 추가 및 업데이트할 레스토랑 리스트
        List<RestaurantPayload> toInsert = new ArrayList<>();
        List<RestaurantPayload> toUpdate = new ArrayList<>();

        for (RestaurantPayload restaurant : restaurants) {
            String managementId = restaurant.getManagementId();

            if (existingRestaurantMap.containsKey(managementId)) {
                // 기존 엔티티가 있을 때, 업데이트 필요 확인
                RestaurantOverview existing = existingRestaurantMap.get(managementId);

                // 새 엔티티가 더 최신 정보를 반영하고 있을 때
                if (existing.getApiUpdatedAt().isBefore(restaurant.getApiUpdatedAt())) {
                    toUpdate.add(restaurant); // 업데이트 목록에 추가
                }

            } else {
                // 신규 엔티티인 경우
                existingRestaurantMap.put(managementId, new RestaurantOverview(restaurant));
                toInsert.add(restaurant); // 신규 추가
            }
        }

        // Bulk Insert
        bulkInsert(toInsert);

        // Bulk Update
        bulkUpdate(toUpdate);
    }

    private void bulkInsert(List<RestaurantPayload> restaurants) {
        String sql = """
                INSERT INTO restaurant (
                    management_id, name, address, location, closed_date, phone_number, type, api_updated_at, region_id 
                ) VALUES (
                    ?, ?, ?, ST_GeomFromText(?, 4326), ?, ?, ?, ?, ?
                )
                """;

        List<Object[]> batchArgs = restaurants.stream()
                .map(restaurant -> new Object[]{
                        restaurant.getManagementId(),
                        restaurant.getName(),
                        restaurant.getAddress(),
                        restaurant.getLocation() != null ? restaurant.getLocation().toText() : null,
                        restaurant.getClosedDate(),
                        restaurant.getPhoneNumber(),
                        restaurant.getType().getTitle(),
                        restaurant.getApiUpdatedAt(),
                        restaurant.getRegionId()
                })
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void bulkUpdate(List<RestaurantPayload> restaurants) {
        String sql = """
                UPDATE 
                    restaurant 
                SET name = ?, address = ?, location = ST_GeomFromText(?, 4326), closed_date = ?, 
                    phone_number = ?, type = ?, api_updated_at = ?, region_id = ?
                WHERE management_id = ?
                """;

        List<Object[]> batchArgs = restaurants.stream()
                .map(restaurant -> new Object[]{
                        restaurant.getName(),
                        restaurant.getAddress(),
                        restaurant.getLocation() != null ? restaurant.getLocation().toText() : null,
                        restaurant.getClosedDate(),
                        restaurant.getPhoneNumber(),
                        restaurant.getType().getTitle(),
                        restaurant.getApiUpdatedAt(),
                        restaurant.getRegionId(),
                        restaurant.getManagementId()
                })
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
