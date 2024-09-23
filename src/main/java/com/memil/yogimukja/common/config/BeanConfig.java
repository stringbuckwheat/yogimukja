package com.memil.yogimukja.common.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }
}
