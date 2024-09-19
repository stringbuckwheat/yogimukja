package com.memil.yogimukja.common.config;

import com.memil.yogimukja.batch.RestaurantProcessor;
import com.memil.yogimukja.batch.RestaurantReader;
import com.memil.yogimukja.batch.RestaurantWriter;
import com.memil.yogimukja.batch.dto.ApiResponse;
import com.memil.yogimukja.restaurant.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {
    private final RestaurantReader restaurantReader;
    private final RestaurantProcessor restaurantProcessor;
    private final RestaurantWriter restaurantWriter;

    @Bean
    public Job restaurantDataJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("Restaurants3", jobRepository)
                .start(restaurant3(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step restaurant3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("restaurant3steps", jobRepository)
                .<List<ApiResponse.Row>, List<Restaurant>>chunk(2, transactionManager) // 2000개 씩 INSERT
                .reader(restaurantReader)
                .processor(restaurantProcessor)
                .writer(restaurantWriter)
                .taskExecutor(taskExecutor()) // 병렬 처리에 사용할 TaskExecutor 설정
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
