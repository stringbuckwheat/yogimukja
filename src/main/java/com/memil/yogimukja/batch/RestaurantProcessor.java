package com.memil.yogimukja.batch;

import com.memil.yogimukja.batch.dto.ApiResponse;
import com.memil.yogimukja.batch.dto.RestaurantPayload;
import com.memil.yogimukja.restaurant.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.proj4j.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantProcessor implements ItemProcessor<List<ApiResponse.Row>, List<RestaurantPayload>> {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public List<RestaurantPayload> process(List<ApiResponse.Row> rows) {
        return rows.parallelStream()
                .filter(this::isValid)
                .map(this::mapToRestaurant)
                .toList();
    }

    private boolean isValid(ApiResponse.Row row) {
        return row != null
                && row.getRdnWhlAddr() != null && !row.getRdnWhlAddr().isEmpty()
                && row.getY() != null && !row.getY().isEmpty()
                && row.getX() != null && !row.getX().isEmpty();
    }

    private RestaurantPayload mapToRestaurant(ApiResponse.Row item) {
        ProjCoordinate location = convertToWGS84(item.getX(), item.getY());
        Point point = geometryFactory.createPoint(new Coordinate(location.x, location.y));

        return new RestaurantPayload(item, point);
    }

    public ProjCoordinate convertToWGS84(String x, String y) {
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
}
