package com.memil.yogimukja.batch.dto;

import com.memil.yogimukja.restaurant.entity.Region;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class RestaurantPayload {
    private String name;
    private String managementId; // 관리 번호
    private String address;
    private Point location; // 공간 데이터 타입
    private LocalDate closedDate; // 폐업일
    private String phoneNumber; // 전화번호
    private String restaurantType; // 업태 구분명
    private LocalDateTime apiUpdatedAt; // API 수정일
    private Long regionId;


    public RestaurantPayload(ApiResponse.Row item, Point point) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        this.name = item.getBplcNm();
        this.address = item.getRdnWhlAddr();
        this.location = point;
        this.regionId = item.getOpnsfTeamCode() != null ? Long.parseLong(item.getOpnsfTeamCode()) : null;
        this.managementId = item.getMgtNo();
        this.phoneNumber = item.getSiteTel();
        this.restaurantType = item.getUptAenM();
        this.apiUpdatedAt = LocalDateTime.parse(item.getUpdateDt(), formatter);

        // 폐업일
        String dateString = item.getDcbYmd().trim();

        if (!dateString.isEmpty()) {
            if (dateString.matches("\\d{8}")) { // 20220320 형식
                closedDate = LocalDate.parse(dateString, formatter1);
            } else if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) { // 2022-03-18 형식
                closedDate = LocalDate.parse(dateString, formatter2);
            } else {
                // 예외 처리 또는 기본값 설정
                throw new IllegalArgumentException("Invalid date format: " + dateString);
            }
        } else {
            this.closedDate = null; // 빈 문자열이나 null 처리
        }
    }
}
