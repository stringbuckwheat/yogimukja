package com.memil.yogimukja.restaurant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private Double latitude; // 위도
    private Double longitude; // 경도

    private String managementNo; // 관리 번호
    private String closedDate; // 폐업일
    private String phoneNumber; // 전화번호
    private String restaurantType; // 업태 구분명
    private String homepage; // 홈페이지


    @Builder
    public Restaurant(String name, String address, Double latitude, Double longitude, String managementNo, String closedDate, String phoneNumber, String restaurantType, String homepage) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.managementNo = managementNo;
        this.closedDate = closedDate;
        this.phoneNumber = phoneNumber;
        this.restaurantType = restaurantType;
        this.homepage = homepage;
    }

    public void setNoLocation() {
        this.latitude = null;
        this.longitude = null;
    }

    public void update(Restaurant restaurant) {
        this.name = restaurant.name;
        this.address = restaurant.address;
        this.latitude = restaurant.latitude;
        this.longitude = restaurant.longitude;

        this.managementNo = restaurant.getManagementNo();
        this.closedDate = restaurant.getClosedDate();
        this.phoneNumber = restaurant.getPhoneNumber();
        this.restaurantType = restaurant.getRestaurantType();
        this.homepage = restaurant.getHomepage();
    }
}
