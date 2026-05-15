package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bellliu
 * @date 2021/11/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportImportantNodeOutDto implements Serializable {

    /**
     * 指标名称
     */
    private String nodeName;

    /**
     * 指标日期
     */
    private String examDate;

    /**
     * 指标结果
     */
    private Double nodeValue;
}
