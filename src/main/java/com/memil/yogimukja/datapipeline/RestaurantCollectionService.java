package com.memil.yogimukja.datapipeline;

import com.memil.yogimukja.datapipeline.dto.ApiResponse;
import com.memil.yogimukja.restaurant.entity.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class RestaurantCollectionService {

    private final WebClient webClient;

    public RestaurantCollectionService(WebClient.Builder webClientBuilder,
                                       @Value("${api.seoul.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("http://openapi.seoul.go.kr:8088/" + apiKey + "/json/LOCALDATA_072404")
                .build();
    }

    public Mono<List<Restaurant>> collectDataFromAPI(String apiUrl) {
        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(this::processResponse);
    }

    private List<Restaurant> processResponse(ApiResponse response) {
        return response.getLocaldata().getRow().stream()
                .map(this::mapToRestaurant)
                .collect(Collectors.toList());
    }

    private Restaurant mapToRestaurant(ApiResponse.Row item) {
        ProjCoordinate location = convertToWGS84(item.getX(), item.getY());
        return Restaurant.builder()
                .name(item.getBplcNm())
                .address(item.getRdnWhlAddr())
                .latitude(location == null ? null : location.y)
                .longitude(location == null ? null : location.x)
                .isOutOfBusiness(item.getDcbYmd() == null)
                .build();
    }

    public ProjCoordinate convertToWGS84(String x, String y) {
        if (!StringUtils.hasText(x) || !StringUtils.hasText(y)) {
            return null;
        }

        Double longitude = Double.parseDouble(x);
        Double latitude = Double.parseDouble(y);

        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem epsg5174 = crsFactory.createFromParameters("epsg:5174",
                "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.4");

        CoordinateReferenceSystem epsg4326 = crsFactory.createFromName("epsg:4326");
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform wgsToUtm = ctFactory.createTransform(epsg5174, epsg4326);

        ProjCoordinate result = new ProjCoordinate();
        wgsToUtm.transform(new ProjCoordinate(longitude, latitude), result);

        return result;
    }
}

