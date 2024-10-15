package com.memil.yogimukja.recommend.scheduler;

import com.memil.yogimukja.recommend.dto.RecommendMessage;
import com.memil.yogimukja.recommend.dto.Recommendation;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantResponse;
import com.memil.yogimukja.restaurant.enums.RestaurantSort;
import com.memil.yogimukja.restaurant.enums.RestaurantType;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import com.memil.yogimukja.user.entity.User;
import com.memil.yogimukja.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class LunchRecommendationScheduler {
    private final UserRepository userRepository;
    private final RestaurantQueryRepository restaurantQueryRepository;
    private final WebClient webClient;

    private static final List<String> EXCLUDE_TYPES = List.of("술집", "카페/디저트");
    private static final List<RestaurantType> VALID_RESTAURANT_TYPE = Stream.of(RestaurantType.values())
            .filter(type -> !EXCLUDE_TYPES.contains(type.name())).toList();

    public LunchRecommendationScheduler(WebClient.Builder webClientBuilder,
                                        UserRepository userRepository,
                                        RestaurantQueryRepository restaurantQueryRepository) {
        this.webClient = webClientBuilder.baseUrl("https://discord.com").build();
        this.userRepository = userRepository;
        this.restaurantQueryRepository = restaurantQueryRepository;
    }

    // 스케줄링: 11시 30분
    @Scheduled(cron = "0 30 11 * * ?")
    public void sendLunchRecommendations() {
        List<User> users = userRepository.findByWebHookUrlIsNotNullAndLocationIsNotNull();

        // 응답 생성
        List<Recommendation> recommendations = createRecommendationsForAllUsers(users);

        // 비동기 전송
        sendRecommendations(recommendations).subscribe(
                nullValue -> log.info("모든 메시지 전송 완료"),
                error -> log.error("메시지 전송 중 오류 발생", error)
        );
    }

    private List<Recommendation> createRecommendationsForAllUsers(List<User> users) {
        return users.stream()
                .map(user -> {
                    List<RestaurantResponse> restaurants = getRecommendableBy(user.getLocation());
                    String message = createMessage(user.getName(), restaurants);
                    return new Recommendation(user.getWebHookUrl(), message);
                })
                .toList();
    }

    private String createMessage(String name, List<RestaurantResponse> nearbyRestaurants) {
        StringBuilder messageBuilder = new StringBuilder();
        String date = new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());

        messageBuilder.append("👍 ").append(name).append("님의 ").append(date).append(" 점심 추천 리스트가 도착했어요!\n\n");

        for (RestaurantResponse restaurant : nearbyRestaurants) {
            String rateInfo = "평점: " + (restaurant.getRate() != null ? restaurant.getRate() : "N/A");
            String reviewInfo = "리뷰 수: " + (restaurant.getReviewCount() > 0 ? restaurant.getReviewCount() : "아직 리뷰가 없습니다.");

            messageBuilder.append("**").append(restaurant.getName()).append("**\n")
                    .append(restaurant.getAddress()).append("\n")
                    .append(rateInfo).append("\n")
                    .append(reviewInfo).append("\n\n");
        }

        // 최종 메시지
        return messageBuilder.toString();
    }

    private List<RestaurantResponse> getRecommendableBy(Point location) {
        RestaurantQueryParams queryParams = RestaurantQueryParams.builder()
                .latitude(location.getY())
                .longitude(location.getX())
                .range(500.0)
                .sort(RestaurantSort.RATING)
                .pageable(PageRequest.of(0, 5))
                .type(VALID_RESTAURANT_TYPE)
                .build();

        return restaurantQueryRepository.findBy(queryParams);
    }

    private Mono<Void> sendRecommendations(List<Recommendation> recommendations) {
        return Flux.fromIterable(recommendations)
                .flatMap(rec -> sendMessage(rec.getWebHookUrl(), rec.getMessage())
                        .subscribeOn(Schedulers.boundedElastic()) // 병렬 처리
                        .doOnError(error -> log.error("메시지 전송 실패", error))
                )
                .then();
    }

    // 메시지 전송
    private Mono<String> sendMessage(String webHookUrl, String content) {
        return webClient.post()
                .uri(webHookUrl)
                .bodyValue(new RecommendMessage(content))
                .retrieve()
                .bodyToMono(String.class);
    }
}
