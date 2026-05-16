package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bellliu
 * @date 2021/11/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthManageReportOutDto implements Serializable {
    /**
     * 报告基础信息
     */
    private ReportBaseInfoDto baseInfo;
    /**
     * 用户汇总信息
     */
    private UserHealthInfo healthInfo;

    /**
     * 健康分
     */
    private HealthGradeDto grade;

    /**
     * 重要指标趋势
     */
    private List<ReportImportNodeTrendOutDto> trend;

    /**
     * 风险疾病
     */
    private List<ReportRiskDiseaseOutDto> riskDiseases;

    /**
     * 饮食
     */
    private FoodReportOutDto food;

    /**
     * 生活方式
     */
    private ReportLiveOutDto live;

    /**
     * 运动
     */
    private SportReportOutDto sport;

    /**
     * 心理测评
     */
    private PsychologyOutDto psychology;

    /**
     * 风险因素及建议
     */
    private RiskAdviceDto advice;

    /**
     * 推荐检查项
     */
    private ExamReportOutDto exam;

    /**
     * 重要指标对比数据
     */
    private CompareReportDto compare;

    /**
     * 原始解读报告
     */
    private ResolveReportDto report;

    //支持自定义多报告
    private List<CompareReportDto> compareList = new LinkedList<>();
    private List<ResolveReportDto> reportList = new LinkedList<>();

}
