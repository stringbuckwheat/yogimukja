package com.memil.yogimukja.review.model;

import com.memil.yogimukja.common.model.BaseEntity;
import com.memil.yogimukja.restaurant.model.Restaurant;
import com.memil.yogimukja.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "restaurant_id"})})

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "음식점 정보는 필수값입니다.")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "리뷰를 작성하려면 사용자 정보가 필요합니다.")
    private User user;

    @NotNull(message = "점수는 필수값입니다.")
    @Min(value = 1, message = "점수는 1 이상이어야 합니다.")
    @Max(value = 5, message = "점수는 5 이하여야 합니다.")
    private Integer rate; // 점수

    private String content; // 내용

    @Builder
    public Review(Restaurant restaurant, User user, Integer rate, String content) {
        this.restaurant = restaurant;
        this.user = user;
        this.rate = rate;
        this.content = content;
    }

    public void update(Integer rate, String content) {
        this.rate = rate;
        this.content = content;
    }
}