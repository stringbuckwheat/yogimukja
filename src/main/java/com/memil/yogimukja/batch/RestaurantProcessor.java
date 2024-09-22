package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.ApiResponse;
import com.memil.yogimukja.batch.dto.RestaurantPayload;
import com.memil.yogimukja.restaurant.enums.RestaurantType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.proj4j.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantProcessor implements ItemProcessor<List<ApiResponse.Row>, List<RestaurantPayload>> {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    private static final List<String> EXCLUDED_TYPES = Arrays.asList(
            "이동조리",
            "출장조리",
            "키즈카페",
            "라이브카페",
            "식품소분업",
            "일반조리판매",
            "식품등 수입판매업",
            "기타 휴게음식점"
    );

    @Override
    public List<RestaurantPayload> process(List<ApiResponse.Row> rows) {
        return rows.parallelStream()
                .filter(this::isValid)
                .map(this::mapToRestaurant)
                .toList();
    }

    private boolean isValid(ApiResponse.Row row) {
        LocalDate closedDate = parseClosedDate(row.getDcbYmd());
        boolean isClosedDateRecent = closedDate == null || closedDate.isAfter(LocalDate.now().minusYears(5));

        return row != null
                && row.getRdnWhlAddr() != null && !row.getRdnWhlAddr().isEmpty()
                && row.getY() != null && !row.getY().isEmpty()
                && row.getX() != null && !row.getX().isEmpty()
                && isClosedDateRecent // 폐업한 가게는 5년 전 정보까지만 허용
                && !EXCLUDED_TYPES.contains(row.getUptAenM()); // 타입 필터링 추가
    }

    private RestaurantPayload mapToRestaurant(ApiResponse.Row item) {
        ProjCoordinate location = convertToWGS84(item.getX(), item.getY());
        Point point = geometryFactory.createPoint(new Coordinate(location.x, location.y));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        return RestaurantPayload.builder()
                .name(item.getBplcNm())
                .address(item.getRdnWhlAddr())
                .location(point)
                .regionId(item.getOpnsfTeamCode() != null ? Long.parseLong(item.getOpnsfTeamCode()) : null)
                .managementId(item.getMgtNo())
                .phoneNumber(item.getSiteTel())
                .type(RestaurantType.getType(item.getUptAenM().trim()))
                .apiUpdatedAt(LocalDateTime.parse(item.getUpdateDt(), formatter))
                .closedDate(parseClosedDate(item.getDcbYmd()))
                .build();
    }

    private ProjCoordinate convertToWGS84(String x, String y) {
        double longitude = Double.parseDouble(x);
        double latitude = Double.parseDouble(y);

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

    private LocalDate parseClosedDate(String rawDateString) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String dateString = rawDateString.trim();

        if (dateString.isEmpty()) {
            return null; // 빈 문자열 처리
        }

        if (dateString.matches("\\d{8}")) { // 20220320 형식
            return LocalDate.parse(dateString, formatter1);
        } else if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) { // 2022-03-18 형식
            return LocalDate.parse(dateString, formatter2);
        }

        return null; // 형식이 맞지 않는 경우
    }
}
