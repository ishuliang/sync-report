package com.helianhealth.family.he.application.syncreport;

import com.helianhealth.family.he.admin.db.entity.SyncFailure;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/sync-failures")
public class SyncFailureController {

    private final SyncFailureRetryService retryService;

    public SyncFailureController(SyncFailureRetryService retryService) {
        this.retryService = retryService;
    }

    @GetMapping
    public List<SyncFailure> list(@RequestParam(required = false) String month) {
        validateMonth(month);
        return retryService.listUnresolved(month);
    }

    @PostMapping("/retry/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SyncFailureRetryService.RetryResult retryOne(@PathVariable Long id) {
        return retryService.retryOne(id);
    }

    @PostMapping("/retry")
    @ResponseStatus(HttpStatus.OK)
    public SyncFailureRetryService.RetrySummary retryBatch(@RequestParam(required = false) String month) {
        validateMonth(month);
        return retryService.retryUnresolved(month);
    }

    private void validateMonth(String month) {
        if (month == null || month.trim().isEmpty()) {
            return;
        }
        try {
            YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must use yyyy-MM format");
        }
    }
}
