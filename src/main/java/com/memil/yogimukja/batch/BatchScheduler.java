package com.memil.yogimukja.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job restaurantDataJob;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    public void runBatchJob() {
        try {
            JobParametersBuilder params = new JobParametersBuilder();
            params.addString("runId", String.valueOf(System.currentTimeMillis()));
            jobLauncher.run(restaurantDataJob, params.toJobParameters());

            log.info("배치 작업 시작!");
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("배치 작업 실행 중 오류 발생: " + e.getMessage());
        }
    }
}
