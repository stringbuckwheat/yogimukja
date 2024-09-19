package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.ApiResponse;
import com.memil.yogimukja.restaurant.entity.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class RestaurantProcessor implements ItemProcessor<List<ApiResponse.Row>, List<Restaurant>> {

    @Override
    public List<Restaurant> process(List<ApiResponse.Row> rows) throws Exception {
        // 데이터 처리 로직
        return rows.parallelStream()
                .map(this::mapToRestaurant)
                .toList();
    }

    private Restaurant mapToRestaurant(ApiResponse.Row item) {
        ProjCoordinate location = convertToWGS84(item.getX(), item.getY());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        return Restaurant.builder()
                .name(item.getBplcNm())
                .address(item.getRdnWhlAddr())
                .latitude(location == null ? null : location.y)
                .longitude(location == null ? null : location.x)
                .regionCode(item.getOpnsfTeamCode())
                .managementNo(item.getMgtNo())
                .closedDate(item.getDcbYmd())
                .phoneNumber(item.getSiteTel())
                .restaurantType(item.getUptAenM())
                .apiUpdatedAt(LocalDateTime.parse(item.getUpdateDt(), formatter))
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
                "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43");
        CoordinateReferenceSystem epsg4326 = crsFactory.createFromName("epsg:4326");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform wgsToUtm = ctFactory.createTransform(epsg5174, epsg4326);

        ProjCoordinate result = new ProjCoordinate();
        wgsToUtm.transform(new ProjCoordinate(longitude, latitude), result);

        return result;
    }
}
