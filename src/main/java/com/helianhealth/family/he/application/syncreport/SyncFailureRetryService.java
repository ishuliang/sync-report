package com.helianhealth.family.he.application.syncreport;

import com.google.gson.Gson;
import com.helianhealth.family.he.admin.db.MyBatisUtil;
import com.helianhealth.family.he.admin.db.entity.SyncFailure;
import com.helianhealth.family.he.admin.db.mapper.SyncFailureMapper;
import com.helianhealth.family.he.admin.model.report.param.CustomerAndManualReportParam;
import com.helianhealth.family.he.admin.service.syncreport.GwtjReportService;
import lombok.Data;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyncFailureRetryService {

    private static final Gson GSON = new Gson();

    private final String userId;
    private final String token;
    private final String stationId;

    public SyncFailureRetryService(@Value("${gateway.userId:}") String userId,
                                   @Value("${gateway.token:}") String token,
                                   @Value("${gateway.stationId:}") String stationId) {
        this.userId = userId;
        this.token = token;
        this.stationId = stationId;
    }

    public List<SyncFailure> listUnresolved(String month) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncFailureMapper mapper = session.getMapper(SyncFailureMapper.class);
            if (month == null || month.trim().isEmpty()) {
                return mapper.selectUnresolved();
            }
            return mapper.selectUnresolvedByMonth(month);
        }
    }

    public RetryResult retryOne(Long id) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncFailureMapper mapper = session.getMapper(SyncFailureMapper.class);
            SyncFailure failure = mapper.selectById(id);
            if (failure == null) {
                return RetryResult.skipped(id, null, "failure record not found");
            }
            return retryOne(mapper, failure);
        }
    }

    public RetrySummary retryUnresolved(String month) {
        List<RetryResult> results = new ArrayList<>();
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncFailureMapper mapper = session.getMapper(SyncFailureMapper.class);
            List<SyncFailure> failures = (month == null || month.trim().isEmpty())
                    ? mapper.selectUnresolved()
                    : mapper.selectUnresolvedByMonth(month);
            for (SyncFailure failure : failures) {
                results.add(retryOne(mapper, failure));
            }
        }
        return RetrySummary.from(results);
    }

    private RetryResult retryOne(SyncFailureMapper mapper, SyncFailure failure) {
        Long id = failure.getId();
        if (failure.getResolved() != null && failure.getResolved() == 1) {
            return RetryResult.skipped(id, failure.getCustomerName(), "already resolved");
        }
        if (!"PUSH_GATEWAY".equals(failure.getStage())) {
            return RetryResult.skipped(id, failure.getCustomerName(),
                    "stage " + failure.getStage() + " cannot be retried automatically");
        }
        try {
            CustomerAndManualReportParam param =
                    GSON.fromJson(failure.getPayload(), CustomerAndManualReportParam.class);
            new GwtjReportService().sendToGateway(param, userId, token, stationId);
            mapper.markResolved(id);
            return RetryResult.success(id, failure.getCustomerName());
        } catch (Exception e) {
            mapper.incrementRetry(id, truncate(e.getMessage(), 2000));
            return RetryResult.failed(id, failure.getCustomerName(), e.getMessage());
        }
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() > max ? value.substring(0, max) : value;
    }

    @Data
    public static class RetrySummary {
        private int total;
        private int success;
        private int failed;
        private int skipped;
        private List<RetryResult> results;

        static RetrySummary from(List<RetryResult> results) {
            RetrySummary summary = new RetrySummary();
            summary.results = results;
            summary.total = results.size();
            for (RetryResult result : results) {
                if ("SUCCESS".equals(result.status)) {
                    summary.success++;
                } else if ("FAILED".equals(result.status)) {
                    summary.failed++;
                } else {
                    summary.skipped++;
                }
            }
            return summary;
        }
    }

    @Data
    public static class RetryResult {
        private Long id;
        private String customerName;
        private String status;
        private String message;

        static RetryResult success(Long id, String customerName) {
            RetryResult result = new RetryResult();
            result.id = id;
            result.customerName = customerName;
            result.status = "SUCCESS";
            result.message = "resolved";
            return result;
        }

        static RetryResult failed(Long id, String customerName, String message) {
            RetryResult result = new RetryResult();
            result.id = id;
            result.customerName = customerName;
            result.status = "FAILED";
            result.message = message;
            return result;
        }

        static RetryResult skipped(Long id, String customerName, String message) {
            RetryResult result = new RetryResult();
            result.id = id;
            result.customerName = customerName;
            result.status = "SKIPPED";
            result.message = message;
            return result;
        }
    }
}
