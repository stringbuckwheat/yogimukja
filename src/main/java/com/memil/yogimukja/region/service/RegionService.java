package com.memil.yogimukja.region.service;

import com.memil.yogimukja.region.model.CachedRegion;

import java.util.List;

public interface RegionService {
    /**
     * 캐시된 지역 정보 조회
     * 캐시 내역이 없으면, 리포지토리에서 지역 정보를 가져와 캐싱
     *
     * @return {@link CachedRegion} 객체의 리스트
     */

    List<CachedRegion> getAll();
}
