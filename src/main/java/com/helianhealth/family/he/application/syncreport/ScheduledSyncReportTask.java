package com.helianhealth.family.he.application.syncreport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ScheduledSyncReportTask {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final SyncReportJobService jobService;
    private final ZoneId zoneId;

    public ScheduledSyncReportTask(SyncReportJobService jobService,
                                   @Value("${syncReport.zone:Asia/Shanghai}") String zoneId) {
        this.jobService = jobService;
        this.zoneId = ZoneId.of(zoneId);
    }

    @Scheduled(cron = "${syncReport.cron:0 0 2 1 * ?}", zone = "${syncReport.zone:Asia/Shanghai}")
    public void syncPreviousMonth() {
        String month = LocalDate.now(zoneId).minusMonths(1).format(MONTH_FORMATTER);
        log.info("Scheduled sync report triggered, month={}", month);
        jobService.submit(month, "schedule:previousMonth");
    }
}
