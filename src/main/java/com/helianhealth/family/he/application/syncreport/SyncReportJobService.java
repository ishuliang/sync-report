package com.helianhealth.family.he.application.syncreport;

import com.helianhealth.family.he.admin.service.syncreport.GwtjReportService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class SyncReportJobService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "sync-report-job");
        thread.setDaemon(false);
        return thread;
    });
    private final AtomicReference<JobStatus> latest = new AtomicReference<>();

    public JobStatus submit(String month, String trigger) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("month must use yyyy-MM format");
        }

        JobStatus current = latest.get();
        if (current != null && current.isRunning()) {
            return current;
        }

        JobStatus next = JobStatus.started(month, trigger);
        if (!latest.compareAndSet(current, next)) {
            return latest.get();
        }

        executor.submit(() -> {
            try {
                log.info("Sync report job started: id={}, month={}, trigger={}",
                        next.getId(), next.getMonth(), next.getTrigger());
                new GwtjReportService().syncReport(month);
                next.succeed();
                log.info("Sync report job finished: id={}, month={}", next.getId(), next.getMonth());
            } catch (Exception e) {
                next.fail(e.getMessage());
                log.error("Sync report job failed: id={}, month={}", next.getId(), next.getMonth(), e);
            }
        });
        return next;
    }

    public JobStatus getLatest() {
        return latest.get();
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    public enum State {
        RUNNING,
        SUCCESS,
        FAILED
    }

    @Getter
    public static class JobStatus {
        private final String id;
        private final String month;
        private final String trigger;
        private final LocalDateTime startTime;
        private volatile State state;
        private volatile LocalDateTime endTime;
        private volatile String message;

        private JobStatus(String id, String month, String trigger, LocalDateTime startTime, State state) {
            this.id = id;
            this.month = month;
            this.trigger = trigger;
            this.startTime = startTime;
            this.state = state;
        }

        static JobStatus started(String month, String trigger) {
            return new JobStatus(UUID.randomUUID().toString(), month, trigger, LocalDateTime.now(), State.RUNNING);
        }

        public boolean isRunning() {
            return state == State.RUNNING;
        }

        void succeed() {
            this.state = State.SUCCESS;
            this.endTime = LocalDateTime.now();
            this.message = "finished";
        }

        void fail(String message) {
            this.state = State.FAILED;
            this.endTime = LocalDateTime.now();
            this.message = message;
        }
    }
}
