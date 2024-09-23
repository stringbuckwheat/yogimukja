package com.memil.yogimukja.region.controller;

import com.memil.yogimukja.region.model.CachedRegion;
import com.memil.yogimukja.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    /**
     * 지역 정보(서울의 '구') 반환
     * 기본적으로 캐시 내역을 반환하되, 캐시된 정보가 없으면 repository에서 가져와 캐싱
     * @return {@link CachedRegion} 객체의 리스트
     */
    @GetMapping("/api/region")
    public ResponseEntity<List<CachedRegion>> getAll() {
        return ResponseEntity.ok().body(regionService.getAll());
    }
}
