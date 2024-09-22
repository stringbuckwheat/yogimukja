package com.memil.yogimukja.recommend.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memil.yogimukja.recommend.service.DiscordWebhookServiceImpl;
import com.memil.yogimukja.restaurant.dto.RestaurantQueryParams;
import com.memil.yogimukja.restaurant.dto.RestaurantSummary;
import com.memil.yogimukja.restaurant.enums.RestaurantSort;
import com.memil.yogimukja.restaurant.enums.RestaurantType;
import com.memil.yogimukja.restaurant.repository.RestaurantQueryRepository;
import com.memil.yogimukja.user.entity.User;
import com.memil.yogimukja.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class LunchRecommendationScheduler {
    private final UserRepository userRepository;
    private final RestaurantQueryRepository restaurantQueryRepository;
    private final DiscordWebhookServiceImpl discordWebhookService;
    private static final List<String> EXCLUDE_TYPES = List.of("술집", "카페/디저트");

    // TODO 점심 추천에서 카페, 술집 이런 건 제외


    // 스케줄링: 11시 30분
    @Scheduled(cron = "0 30 11 * * ?")
    public void sendLunchRecommendations() {
        List<User> allUsers = userRepository.findAll();

        // 점심 추천을 사용하는 유저에게만 메시지 전송
        allUsers.forEach((user) -> {
            if (user.getWebHookUrl() != null) {
                String webhookUrl = user.getWebHookUrl();

                List<RestaurantType> validRestaurantType = Stream.of(RestaurantType.values())
                        .filter(type -> !EXCLUDE_TYPES.contains(type.name())).toList();

                RestaurantQueryParams queryParams = RestaurantQueryParams.builder()
                        .latitude(user.getLatitude())
                        .longitude(user.getLongitude())
                        .range(500.0)
                        .sort(RestaurantSort.RATING)
                        .pageable(PageRequest.of(0, 5))
                        .type(validRestaurantType)
                        .build();

                List<RestaurantSummary> restaurants = restaurantQueryRepository.findRestaurants(queryParams);

                String message = sendPlainTextMessage(user.getName(), restaurants);
                discordWebhookService.sendMessage(webhookUrl, message).subscribe();
            }
        });
    }

    public String sendPlainTextMessage(String name, List<RestaurantSummary> nearbyRestaurants) {
        StringBuilder messageBuilder = new StringBuilder();
        String date = new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());

        messageBuilder.append("👍 ").append(name).append("님의 ").append(date).append(" 점심 추천 리스트가 도착했어요!\n\n");

        for (RestaurantSummary restaurant : nearbyRestaurants) {
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
}

