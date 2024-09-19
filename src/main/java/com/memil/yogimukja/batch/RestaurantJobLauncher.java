package com.memil.yogimukja.batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class RestaurantJobLauncher {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job restaurantDataJob;

    @GetMapping("/run")
    public ResponseEntity<String> runRestaurantJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runId", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            jobLauncher.run(restaurantDataJob, params);
            return ResponseEntity.ok("배치 작업 시작");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("배치 작업 실패: " + e.getMessage());
        }
    }
}
