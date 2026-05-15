package com.helianhealth.family.he.health.api.report.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportConclusionItemDto implements Serializable {
    private Integer itemLevel;
    private String briefSummary;
    private String itemSuggest;
}
