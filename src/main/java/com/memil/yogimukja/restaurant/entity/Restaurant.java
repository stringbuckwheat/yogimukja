package com.memil.yogimukja.restaurant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String managementId; // 관리 번호

    private String name;

    private String address;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location; // 공간 데이터 타입

    private LocalDate closedDate; // 폐업일

    private String phoneNumber; // 전화번호

    private String restaurantType; // 업태 구분명

    private LocalDateTime apiUpdatedAt; // API 수정일

    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    private List<Review> reviews = new ArrayList<>();
}
