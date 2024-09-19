package com.memil.yogimukja.batch;
import com.memil.yogimukja.batch.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RestaurantDataFetcher {

    private final WebClient webClient;

    public RestaurantDataFetcher(WebClient.Builder webClientBuilder,
                                 @Value("${api.seoul.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("http://openapi.seoul.go.kr:8088/" + apiKey + "/json/LOCALDATA_072404")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)) // 30MB
                .build();
    }

    public Flux<ApiResponse.Row> fetchData(int start, int end) {
        String apiUrl = "/" + start + "/" + end;

        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToFlux(ApiResponse.class)
                .flatMap(response -> Flux.fromIterable(response.getLocaldata().getRow()));
    }


    public Mono<ApiResponse> fetchInitialData() {
        return webClient.get()
                .uri("/1/2") // 초기 데이터 및 ListTotalCount 값을 반환하는 API 엔드포인트
                .retrieve()
                .bodyToMono(ApiResponse.class);
    }
}
