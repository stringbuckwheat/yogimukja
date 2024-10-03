package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.ApiResponse;
import com.memil.yogimukja.batch.dto.Range;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@DependsOn("restaurantDataFetcher")
public class RestaurantReader implements ItemReader<List<ApiResponse.Row>> {

    private final RestaurantDataFetcher dataFetcher;
    private final ConcurrentLinkedQueue<Range> rangeQueue = new ConcurrentLinkedQueue<>();
    private int end;
    private final int step = 1000;

    public RestaurantReader(RestaurantDataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @PostConstruct
    public void init() {
        // 배치 작업 시작 전에 초기화
        initializeEndValue().block();  // 초기화가 끝날 때까지 대기
    }

    private Mono<Void> initializeEndValue() {
        return dataFetcher.fetchInitialData()
                .map(response -> response.getLocaldata().getListTotalCount())
                .doOnNext(value -> {
                    this.end = value;
                    log.info("API 데이터 끝 지점: {}", end);
                    initializeRanges();
                })
                .then();
    }

    private void initializeRanges() {
        for (int start = 1; start <= end; start += step) {
            int rangeEnd = Math.min(start + step - 1, end);
            rangeQueue.add(new Range(start, rangeEnd));
        }
    }

    @Override
    public List<ApiResponse.Row> read() {
        // end 값이 초기화되지 않았다면 초기화 로직 수행
        if (end == 0) {
            initializeEndValue().block();  // 초기화가 끝날 때까지 대기
        }

        Range range = rangeQueue.poll();

        if (range == null) {
            return null;
        }

        log.info("데이터 요청 범위: {} / {}", range.getStart(), range.getEnd());

        return dataFetcher.fetchData(range.getStart(), range.getEnd())
                .subscribeOn(Schedulers.boundedElastic())
                .collectList()
                .doOnError(e -> log.error("데이터 fetch 중 오류: {}-{}", range.getStart(), range.getEnd(), e))
                .block();
    }
}