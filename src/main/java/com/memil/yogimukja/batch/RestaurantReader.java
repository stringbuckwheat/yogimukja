package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class RestaurantReader implements ItemReader<List<ApiResponse.Row>> {

    private final RestaurantDataFetcher dataFetcher;
    private final ConcurrentLinkedQueue<Range> rangeQueue = new ConcurrentLinkedQueue<>();
    private int end;
    private final int step = 1000;

    public RestaurantReader(RestaurantDataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
        initializeEndValue()
                .doOnSuccess(aVoid -> initializeRanges())
                .block(); // 이 메서드가 완료되기 전까지는 read() 메서드가 호출되지 않도록 보장
    }

    private Mono<Void> initializeEndValue() {
        return dataFetcher.fetchInitialData()
                .map(response -> response.getLocaldata().getListTotalCount())
                .doOnNext(value -> {
                    this.end = value;
                    log.info("Dynamic end value set to: {}", end);
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
        Range range = rangeQueue.poll();
        if (range == null) {
            return null; // 모든 페이지를 읽었다면 null을 반환
        }

        System.out.println(range.getStart() + "/" + range.getEnd());

        return dataFetcher.fetchData(range.getStart(), range.getEnd())
                .subscribeOn(Schedulers.boundedElastic()) // 비동기 처리
                .collectList()
                .doOnError(e -> log.error("Error fetching data for range: {}-{}", range.getStart(), range.getEnd(), e))
                .block(); // 결과를 동기적으로 받기
    }

    private static class Range {
        private final int start;
        private final int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
