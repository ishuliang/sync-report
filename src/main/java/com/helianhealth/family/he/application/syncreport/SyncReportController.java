package com.helianhealth.family.he.application.syncreport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/range/{start}/{end}")
    public ResponseEntity<SyncReportJobService.JobStatus> syncRange(@PathVariable String start,
                                                                    @PathVariable String end) {
        List<String> months = buildMonths(start, end);
        SyncReportJobService.JobStatus status = jobService.submitRange(months, "manual-range");
        return ResponseEntity.status(status.isRunning() ? HttpStatus.ACCEPTED : HttpStatus.OK).body(status);
    }

    private List<String> buildMonths(String start, String end) {
        YearMonth from = parseMonth(start);
        YearMonth to = parseMonth(end);
        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must not be after end");
        }
        List<String> months = new ArrayList<>();
        for (YearMonth cursor = from; !cursor.isAfter(to); cursor = cursor.plusMonths(1)) {
            months.add(cursor.toString());
        }
        return months;
    }

    private YearMonth parseMonth(String month) {
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must use yyyy-MM format");
        }
        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid month: " + month);
        }
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
