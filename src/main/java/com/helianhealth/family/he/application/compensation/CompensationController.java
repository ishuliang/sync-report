package com.helianhealth.family.he.application.compensation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compensations")
public class CompensationController {

    private final CompensationJobService jobService;

    public CompensationController(CompensationJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/stored-task")
    public ResponseEntity<CompensationJobService.JobStatus> compensateByStoredTask() {
        CompensationJobService.JobStatus status = jobService.submitStoredTaskCompensation("manual");
        return ResponseEntity.status(status.isRunning() ? HttpStatus.ACCEPTED : HttpStatus.OK).body(status);
    }

    @PostMapping("/data-prepare")
    public ResponseEntity<CompensationJobService.JobStatus> compensateByDataPrepare() {
        CompensationJobService.JobStatus status = jobService.submitDataPrepareCompensation("manual");
        return ResponseEntity.status(status.isRunning() ? HttpStatus.ACCEPTED : HttpStatus.OK).body(status);
    }

    @GetMapping("/latest")
    public ResponseEntity<CompensationJobService.JobStatus> latest() {
        CompensationJobService.JobStatus status = jobService.getLatest();
        if (status == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(status);
    }
}
