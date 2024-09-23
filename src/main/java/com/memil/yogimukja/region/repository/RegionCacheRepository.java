package com.memil.yogimukja.region.repository;


import com.memil.yogimukja.region.model.CachedRegion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionCacheRepository extends CrudRepository<CachedRegion, Long> {
}
