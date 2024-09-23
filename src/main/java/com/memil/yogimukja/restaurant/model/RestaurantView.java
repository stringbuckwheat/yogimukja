package com.memil.yogimukja.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("restaurantViews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class RestaurantView {
    @Id
    private Long restaurantId;
    private int viewCount;

    public void increaseViewCount() {
        this.viewCount++;
    }
}
