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
    private boolean isOutOfBusiness; // 폐업 여부

    @Builder
    public Restaurant(String name, String address, Double latitude, Double longitude, boolean isOutOfBusiness) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOutOfBusiness = isOutOfBusiness;
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
        this.isOutOfBusiness = restaurant.isOutOfBusiness;
    }
}
