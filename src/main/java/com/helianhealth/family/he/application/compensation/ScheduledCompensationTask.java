package com.helianhealth.family.he.application.compensation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledCompensationTask {

    private final CompensationJobService jobService;

    public ScheduledCompensationTask(CompensationJobService jobService) {
        this.jobService = jobService;
    }

    @Scheduled(cron = "${compensation.dataPrepareCron:0 0 2 * * ?}")
    public void compensateByDataPrepare() {
        log.info("Scheduled dataPrepare compensation triggered");
        jobService.submitDataPrepareCompensation("schedule:dataPrepare");
    }

    @Scheduled(cron = "${compensation.storedTaskCron:-}")
    public void compensateByStoredTask() {
        log.info("Scheduled stored-task compensation triggered");
        jobService.submitStoredTaskCompensation("schedule:storedTask");
    }
}
