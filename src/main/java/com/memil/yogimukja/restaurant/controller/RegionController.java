package com.memil.yogimukja.restaurant.controller;

import com.memil.yogimukja.restaurant.model.CachedRegion;
import com.memil.yogimukja.restaurant.service.RegionServiceImpl;
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
    private final RegionServiceImpl regionService;

    @GetMapping("/api/region")
    public ResponseEntity<List<CachedRegion>> getAll() {
        return ResponseEntity.ok().body(regionService.getAll());
    }
}
