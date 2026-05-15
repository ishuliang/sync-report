package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu
 * @date 2021/11/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRiskDiseaseOutDto implements Serializable {

    /**
     * ICD编码
     */
    private String icd;

    /**
     * 名称
     */
    private String name;

    /**
     * 风险等级 3高 2中
     */
    private Integer riskLevel;

    /**
     * 影响因素
     */
    private List<String> factor;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 预防要点
     */
    private String adv;
}
