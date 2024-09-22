package com.memil.yogimukja.batch.dto;

import com.memil.yogimukja.restaurant.enums.RestaurantType;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantPayload {
    private String name;
    private String managementId; // 관리 번호
    private String address;
    private Point location; // 공간 데이터 타입
    private LocalDate closedDate; // 폐업일
    private String phoneNumber; // 전화번호
    private RestaurantType type; // 업태 구분명
    private LocalDateTime apiUpdatedAt; // API 수정일
    private Long regionId;
}
