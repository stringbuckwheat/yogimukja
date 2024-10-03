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
        long startTime = System.currentTimeMillis(); // 시작 시간 기록

        try {
            JobParametersBuilder params = new JobParametersBuilder();
            params.addString("runId", String.valueOf(System.currentTimeMillis()));
            jobLauncher.run(restaurantDataJob, params.toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("배치 작업 실행 중 오류 발생: " + e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis(); // 종료 시간 기록
            long duration = endTime - startTime; // 소요 시간 계산
            log.info("배치 작업 소요 시간: " + duration + " ms");
        }
    }
}
