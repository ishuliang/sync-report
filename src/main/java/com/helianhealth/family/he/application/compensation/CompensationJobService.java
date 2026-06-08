package com.helianhealth.family.he.application.compensation;

import com.helianhealth.family.he.admin.service.syncreport.CompensationService;
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
public class CompensationJobService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "compensation-job");
        thread.setDaemon(false);
        return thread;
    });
    private final AtomicReference<JobStatus> latest = new AtomicReference<>();

    public JobStatus submitStoredTaskCompensation(String trigger) {
        return submit(JobType.STORED_TASK_COMPENSATION, trigger, () -> new CompensationService().compensate());
    }

    public JobStatus submitDataPrepareCompensation(String trigger) {
        return submit(JobType.DATA_PREPARE_COMPENSATION, trigger,
                () -> new CompensationService().compensateByDataPrepare());
    }

    public JobStatus getLatest() {
        return latest.get();
    }

    private JobStatus submit(JobType type, String trigger, Runnable runnable) {
        JobStatus current = latest.get();
        if (current != null && current.isRunning()) {
            return current;
        }

        JobStatus next = JobStatus.started(type, trigger);
        if (!latest.compareAndSet(current, next)) {
            return latest.get();
        }

        executor.submit(() -> {
            try {
                log.info("Compensation job started: id={}, type={}, trigger={}",
                        next.getId(), next.getType(), next.getTrigger());
                runnable.run();
                next.succeed();
                log.info("Compensation job finished: id={}, type={}", next.getId(), next.getType());
            } catch (Exception e) {
                next.fail(e.getMessage());
                log.error("Compensation job failed: id={}, type={}", next.getId(), next.getType(), e);
            }
        });
        return next;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    public enum JobType {
        STORED_TASK_COMPENSATION,
        DATA_PREPARE_COMPENSATION
    }

    public enum State {
        RUNNING,
        SUCCESS,
        FAILED
    }

    @Getter
    public static class JobStatus {
        private final String id;
        private final JobType type;
        private final String trigger;
        private final LocalDateTime startTime;
        private volatile State state;
        private volatile LocalDateTime endTime;
        private volatile String message;

        private JobStatus(String id, JobType type, String trigger, LocalDateTime startTime, State state) {
            this.id = id;
            this.type = type;
            this.trigger = trigger;
            this.startTime = startTime;
            this.state = state;
        }

        static JobStatus started(JobType type, String trigger) {
            return new JobStatus(UUID.randomUUID().toString(), type, trigger, LocalDateTime.now(), State.RUNNING);
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
