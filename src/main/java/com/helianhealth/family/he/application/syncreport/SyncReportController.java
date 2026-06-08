package com.helianhealth.family.he.application.syncreport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync-reports")
public class SyncReportController {

    private final SyncReportJobService jobService;

    public SyncReportController(SyncReportJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/{month}")
    public ResponseEntity<SyncReportJobService.JobStatus> syncMonth(@PathVariable String month) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must use yyyy-MM format");
        }
        SyncReportJobService.JobStatus status = jobService.submit(month, "manual");
        return ResponseEntity.status(status.isRunning() ? HttpStatus.ACCEPTED : HttpStatus.OK).body(status);
    }

    @GetMapping("/latest")
    public ResponseEntity<SyncReportJobService.JobStatus> latest() {
        SyncReportJobService.JobStatus status = jobService.getLatest();
        if (status == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(status);
    }
}
