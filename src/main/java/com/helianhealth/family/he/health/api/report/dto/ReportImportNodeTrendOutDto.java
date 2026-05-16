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
public class ReportImportNodeTrendOutDto implements Serializable {
    /**
     * 名称
     */
    private String name;
    /**
     * 解释
     */
    private String explain;
    /**
     * 判断
     */
    private String judge;
    /**
     * 指标数据
     */
    private List<ReportImportantNodeOutDto> nodeList;
}
