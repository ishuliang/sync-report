package com.helianhealth.family.he.application.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.helianhealth.family.he.admin.model.wgtj.GwtjRecord;
import com.helianhealth.family.he.admin.service.syncreport.GwtjReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Gson GSON = new Gson();

    @Value("${gateway.fid:HL99999}")
    private String defaultFid;

    @Value("${gateway.isPrint:2}")
    private String defaultIsPrint;

    @Value("${gateway.userId:}")
    private String defaultUserId;

    @Value("${gateway.token:}")
    private String defaultToken;

    @Value("${gateway.stationId:}")
    private String defaultStationId;

    @PostMapping("/1")
    public String test01() {
        return "/test/1";
    }

    @PostMapping("/push-json")
    public GwtjReportService.PushRecordsResult pushJson(
            @RequestBody String json,
            @RequestParam String hospitalName,
            @RequestParam(required = false) String fid,
            @RequestParam(required = false) String isPrint,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String stationId) throws Exception {

        List<GwtjRecord> records = GSON.fromJson(json, new TypeToken<List<GwtjRecord>>() {}.getType());
        if (records == null || records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body must be a non-empty JSON array");
        }
        return new GwtjReportService().pushRecords(
                records,
                defaultIfBlank(fid, defaultFid),
                hospitalName,
                defaultIfBlank(userId, defaultUserId),
                defaultIfBlank(token, defaultToken),
                defaultIfBlank(stationId, defaultStationId),
                defaultIfBlank(isPrint, defaultIsPrint)
        );
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }
}
