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
                .block();
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
            log.info("Reading all of the api data");
            return null;
        }

        System.out.println(range.getStart() + "/" + range.getEnd());

        return dataFetcher.fetchData(range.getStart(), range.getEnd())
                .subscribeOn(Schedulers.boundedElastic())
                .collectList()
                .doOnError(e -> log.error("Error fetching data for range: {}-{}", range.getStart(), range.getEnd(), e))
                .block();
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