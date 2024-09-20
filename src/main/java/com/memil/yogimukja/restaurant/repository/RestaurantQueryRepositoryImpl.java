package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.batch.dto.QRestaurantOverview;
import com.memil.yogimukja.batch.dto.RestaurantOverview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.memil.yogimukja.restaurant.entity.QRestaurant.restaurant;

@RequiredArgsConstructor
@Repository
public class RestaurantQueryRepositoryImpl implements RestaurantQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<RestaurantOverview> findAllManagementIdAndApiUpdatedAt() {
        return queryFactory
                .select(
                        new QRestaurantOverview(
                                restaurant.managementId,
                                restaurant.apiUpdatedAt
                        )
                )
                .from(restaurant)
                .fetch();
    }
}
