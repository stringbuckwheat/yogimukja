package com.memil.yogimukja.restaurant.repository;

import com.memil.yogimukja.batch.dto.QRestaurantOverview;
import com.memil.yogimukja.batch.dto.RestaurantOverview;
import com.memil.yogimukja.restaurant.dto.*;
import com.memil.yogimukja.restaurant.enums.RestaurantSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.memil.yogimukja.restaurant.model.QRestaurant.restaurant;
import static com.memil.yogimukja.review.model.QReview.review;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RestaurantQueryRepositoryImpl implements RestaurantQueryRepository {
    private final JPAQueryFactory queryFactory;

    // Batch에서 사용
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

    public Optional<RestaurantResponse> findDetail(Long restaurantId) {
        RestaurantResponse result = queryFactory
                .select(
                        new QRestaurantResponse(
                                restaurant,
                                review.rate.avg(),
                                review.rate.count()
                        )
                )
                .from(restaurant)
                .leftJoin(restaurant.reviews, review)
                .where(restaurant.id.eq(restaurantId))
                .groupBy(restaurant.id)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<RestaurantResponse> findPopular(LocalDateTime startDate, UserLocation userLocation) {
        return queryFactory
                .select(
                        new QRestaurantResponse(
                                restaurant,
                                review.rate.avg(),
                                review.rate.count(),
                                getDistanceTemplate(
                                        restaurant.location,
                                        userLocation.getLatitude(),
                                        userLocation.getLongitude()
                                )
                        )
                ).from(restaurant)
                .join(restaurant.reviews, review)
                .where(review.createdDate.after(startDate))
                .groupBy(restaurant.id)
                .orderBy(review.rate.avg().coalesce(0.0).desc())
                .fetch();
    }

    @Override
    public List<RestaurantResponse> findByRegion(Long regionId, Pageable pageable, UserLocation userLocation) {
        return queryFactory
                .select(
                        new QRestaurantResponse(
                                restaurant,
                                review.rate.avg(),
                                review.rate.count(),
                                getDistanceTemplate(
                                        restaurant.location,
                                        userLocation.getLatitude(),
                                        userLocation.getLongitude()
                                )
                        )
                )
                .from(restaurant)
                .leftJoin(restaurant.reviews, review)
                .where(restaurant.region.id.eq(regionId))
                .groupBy(restaurant.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(
                        review.rate.avg().coalesce(0.0).desc()
                )
                .fetch();
    }

    @Override
    public List<RestaurantResponse> findBy(RestaurantQueryParams queryParams) {
        OrderSpecifier<?> orderBy = createOrderBy(queryParams);
        BooleanBuilder whereClause = createWhereClause(queryParams);

        return queryFactory
                .select(
                        new QRestaurantResponse(
                                restaurant,
                                review.rate.avg(),
                                review.rate.count(),
                                getDistanceTemplate(
                                        restaurant.location,
                                        queryParams.getLatitude(),
                                        queryParams.getLongitude()
                                )
                        )
                ).from(restaurant)
                .leftJoin(restaurant.reviews, review)
                .where(whereClause)
                .groupBy(restaurant.id)
                .orderBy(orderBy)
                .offset(queryParams.getPageable().getOffset())
                .limit(queryParams.getPageable().getPageSize())
                .fetch();
    }

    private NumberTemplate<Double> getDistanceTemplate(ComparablePath<Point> restaurantLocation, Double latitude, Double longitude) {
        // PostGIS ST_DistanceSphere 함수 사용하여 두 지점 간의 거리 계산
        return Expressions.numberTemplate(
                Double.class,
                "ST_DistanceSphere({0}, ST_MakePoint({1}, {2}))",
                restaurantLocation, longitude, latitude
        );
    }

    private OrderSpecifier<?> createOrderBy(RestaurantQueryParams queryParams) {
        // 거리 순
        if (queryParams.getLatitude() != null
                && queryParams.getLongitude() != null
                && queryParams.getSort().equals(RestaurantSort.DISTANCE)) {
            return Expressions.booleanTemplate(
                    "ST_DistanceSphere({0}, ST_MakePoint({1}, {2}))",
                    restaurant.location, queryParams.getLongitude(), queryParams.getLatitude()
            ).asc();
        } else {
            // 평점 순
            return review.rate.avg().coalesce(0.0).desc();
        }
    }

    private BooleanBuilder createWhereClause(RestaurantQueryParams queryParams) {
        // 기본 조건 == 폐업하지 않음
        BooleanBuilder whereClause = new BooleanBuilder().and(restaurant.closedDate.isNull());

        // 위치 조건이 있는 경우
        if (hasLocation(queryParams)) {
            // 해당 반경 내만 검색
            whereClause.and(Expressions.booleanTemplate(
                    "ST_DistanceSphere({0}, ST_MakePoint({1}, {2})) <= {3}",
                    restaurant.location, queryParams.getLongitude(), queryParams.getLatitude(), queryParams.getRange()
            ));
        }

        // 검색어가 있는 경우
        if (hasSearchTerm(queryParams)) {
            whereClause.and(restaurant.name.like("%" + queryParams.getSearch() + "%"));
        }

        // 업종 필터링이 있는 경우(ex. 한식, 중식, 일식...)
        if (hasFilter(queryParams)) {
            // 각각의 LIKE 쿼리 작성
            List<BooleanExpression> likes = queryParams.getType().stream()
                    .map(type -> restaurant.type.eq(type.getTitle())).toList();

            // OR로 연결
            BooleanExpression combinedPredicate = likes.stream().reduce(BooleanExpression::or).orElse(null);

            whereClause.and(combinedPredicate);
        }

        return whereClause;
    }

    private boolean hasLocation(RestaurantQueryParams queryParams) {
        return queryParams.getLatitude() != null
                && queryParams.getLongitude() != null
                && queryParams.getRange() != null;
    }

    private boolean hasSearchTerm(RestaurantQueryParams queryParams) {
        return queryParams.getSearch() != null && !queryParams.getSearch().isEmpty();
    }

    private boolean hasFilter(RestaurantQueryParams queryParams) {
        return queryParams.getType() != null && !queryParams.getType().isEmpty();
    }
}
