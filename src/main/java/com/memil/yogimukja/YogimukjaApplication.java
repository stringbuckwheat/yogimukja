package com.memil.yogimukja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableCaching
public class YogimukjaApplication {

	public static void main(String[] args) {
		SpringApplication.run(YogimukjaApplication.class, args);
	}

}
