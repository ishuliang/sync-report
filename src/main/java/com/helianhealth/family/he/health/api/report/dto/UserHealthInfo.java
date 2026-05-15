package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author bellliu
 * @date 2021/11/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHealthInfo implements Serializable {


    /**
     * 体检异常
     */
    private String exceptionResult;

    /**
     * 既往史-确诊疾病
     */
    private List<ReportDiseaseDto> priorDisease;

    /**
     * 家族史
     */
    private List<ReportDiseaseDto> familyDisease;

    /**
     * 行为特征-分组
     */
    private Map<String,List<String>> behavioralFeatures;

    /**
     * 高风险疾病名称列表
     */
    private List<ReportDiseaseDto> riskyDiseaseList;

    /**
     * 部分风险名称疾病列表
     */
    private List<ReportDiseaseDto> partlyRiskyDiseaseList;


}
