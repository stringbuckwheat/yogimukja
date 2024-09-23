package com.memil.yogimukja.region.service;

import com.memil.yogimukja.region.model.CachedRegion;
import com.memil.yogimukja.region.repository.RegionCacheRepository;
import com.memil.yogimukja.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    private final RegionCacheRepository regionCacheRepository;

    private static final int REGION_COUNT = 25;

    @Override
    public List<CachedRegion> getAll() {
        List<CachedRegion> cachedRegions = new ArrayList<>();
        regionCacheRepository.findAll().forEach(cachedRegions::add);

        if (cachedRegions.size() == REGION_COUNT) {
            return cachedRegions;
        }

        // 캐싱된 지역 정보가 없으면 캐싱
        List<CachedRegion> regions = regionRepository.findAll().stream().map(CachedRegion::new).toList();

        regionCacheRepository.saveAll(regions);

        return regions;
    }
}
