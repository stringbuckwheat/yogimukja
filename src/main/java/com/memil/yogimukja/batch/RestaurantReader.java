package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RestaurantReader implements ItemReader<List<ApiResponse.Row>> {
    private final WebClient webClient;
    private final AtomicInteger currentIndex = new AtomicInteger(1);
    private final AtomicInteger end = new AtomicInteger(1000);
    private final int step = 1000;

    public RestaurantReader(WebClient.Builder webClientBuilder,
                            @Value("${api.seoul.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("http://openapi.seoul.go.kr:8088/" + apiKey + "/json/LOCALDATA_072404")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)) // 30MB
                .build();
    }

    @Override
    public List<ApiResponse.Row> read() {
        log.info(">>>>>>>>> Restaurant Reader ");

        int current = currentIndex.get();
        if (current > end.get()) {
            return null; // 모든 페이지를 읽었다면 null을 반환
        }

        int rangeEnd = Math.min(current + step - 1, end.get());
        String apiUrl = "/" + current + "/" + rangeEnd;

        currentIndex.addAndGet(step); // 다음 범위로 이동

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ApiResponse.LocalData localdata = Objects.requireNonNull(webClient.get()
                        .uri(apiUrl)
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .doFinally(signalType -> stopWatch.stop())
                        .doOnSuccess(response -> log.info("Fetched API response for range: {}-{}", current, rangeEnd))
                        .doOnError(error -> log.error("Error fetching API response for range: {}-{}", current, rangeEnd, error))
                        .block()) // Mono를 블로킹하여 ApiResponse 반환
                .getLocaldata();

        // 마지막 페이지 계산
        end.set(localdata.getListTotalCount());

        return localdata.getRow();
    }
}
