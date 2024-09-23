package com.memil.yogimukja.region.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "region") // 구 정보는 변하지 않으므로 만료시간 설정 X
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
