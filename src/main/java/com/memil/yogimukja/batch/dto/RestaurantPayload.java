package com.memil.yogimukja.batch.dto;

import com.memil.yogimukja.restaurant.entity.Region;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class RestaurantPayload {
    private String name;
    private String managementId; // 관리 번호
    private String address;
    private Point location; // 공간 데이터 타입
    private String closedDate; // 폐업일
    private String phoneNumber; // 전화번호
    private String restaurantType; // 업태 구분명
    private LocalDateTime apiUpdatedAt; // API 수정일
    private Region region;


    public RestaurantPayload(ApiResponse.Row item, Point point, Region region) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        this.name = item.getBplcNm();
        this.address = item.getRdnWhlAddr();
        this.location = point;
        this.region = region;
        this.managementId = item.getMgtNo();
        this.closedDate = item.getDcbYmd();
        this.phoneNumber = item.getSiteTel();
        this.restaurantType = item.getUptAenM();
        this.apiUpdatedAt = LocalDateTime.parse(item.getUpdateDt(), formatter);
    }
}
