package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthGradeDto implements Serializable {

    /**
     * 基础分
     */
    private int baseGrade;

    /**
     * 行为习惯
     */
    private int behaviorGrade;

    /**
     * 生活环境
     */
    private int environmentGrade;

    /**
     * 既往病史
     */
    private int priorDiseasesGrade;

    /**
     * 家族病史
     */
    private int familyDiseasesGrade;

    /**
     * 健康分
     */
    private int healthGrade;

    /**
     * 等级
     */
    private String level;
}
