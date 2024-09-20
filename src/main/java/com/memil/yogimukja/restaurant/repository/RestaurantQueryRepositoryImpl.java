package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.batch.dto.QRestaurantOverview;
import com.memil.yogimukja.batch.dto.RestaurantOverview;
import com.memil.yogimukja.restaurant.dto.QRestaurantSummary;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantSummary;
import com.memil.yogimukja.restaurant.enums.RestaurantSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.memil.yogimukja.restaurant.entity.QRestaurant.restaurant;
import static com.memil.yogimukja.restaurant.entity.QReview.review;

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

    @Override
    public List<RestaurantSummary> findRestaurantsWithinDistance(RestaurantQueryParams queryParams) {
        OrderSpecifier<?> orderBy = createOrderBy(queryParams);
        BooleanBuilder whereClause = createWhereClause(queryParams);

        return queryFactory
                .select(new QRestaurantSummary(restaurant, review.rate.avg(), review.rate.count()))
                .from(restaurant)
                .leftJoin(restaurant.reviews, review)
                .where(whereClause)
                .groupBy(restaurant.id)
                .orderBy(orderBy)
                .fetch();
    }

    private OrderSpecifier<?> createOrderBy(RestaurantQueryParams queryParams) {
        if (queryParams.getSort().equals(RestaurantSort.DISTANCE)) {
            return Expressions.booleanTemplate(
                    "ST_DistanceSphere({0}, ST_MakePoint({1}, {2}))",
                    restaurant.location, queryParams.getLongitude(), queryParams.getLatitude()
            ).asc();
        } else {
            return review.rate.avg().asc();
        }
    }

    private BooleanBuilder createWhereClause(RestaurantQueryParams queryParams) {
        BooleanBuilder whereClause = new BooleanBuilder()
                .and(Expressions.booleanTemplate(
                        "ST_DistanceSphere({0}, ST_MakePoint({1}, {2})) <= {3}",
                        restaurant.location, queryParams.getLongitude(), queryParams.getLatitude(), queryParams.getRange()
                ))
                .and(restaurant.closedDate.isNull());

        if (hasSearchTerm(queryParams)) {
            whereClause.and(restaurant.name.like("%" + queryParams.getSearch() + "%"));
        }

        if (hasFilter(queryParams)) {
            whereClause.and(restaurant.restaurantType.like("%" + queryParams.getFilter() + "%"));
        }

        return whereClause;
    }

    private boolean hasSearchTerm(RestaurantQueryParams queryParams) {
        return queryParams.getSearch() != null && !queryParams.getSearch().isEmpty();
    }

    private boolean hasFilter(RestaurantQueryParams queryParams) {
        return queryParams.getFilter() != null && !queryParams.getFilter().isEmpty();
    }

}
