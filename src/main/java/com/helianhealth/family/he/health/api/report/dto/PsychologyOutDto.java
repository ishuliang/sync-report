package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bellliu
 * @date 2021/11/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PsychologyOutDto implements Serializable {
    /**
     * 压力分析标题
     */
    private String pressureTitle;
    /**
     * 压力分析内容
     */
    private String pressureContent;
    /**
     * 焦虑标题
     */
    private String anxietyTitle;
    /**
     * 焦虑内容
     */
    private String anxietyContent;
    /**
     * 抑郁标题
     */
    private String depressionTitle;
    /**
     * 抑郁内容
     */
    private String depressionContent;
}
