package com.memil.yogimukja.restaurant.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "region")
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CachedRegion {
    @Id
    private Long id;

    private String name;

    public CachedRegion(Region region) {
        this.id = region.getId();
        this.name = region.getName();
    }
}
