package com.memil.yogimukja.restaurant.repository;


import com.memil.yogimukja.restaurant.model.CachedRegion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionCacheRepository extends CrudRepository<CachedRegion, Long> {
}
