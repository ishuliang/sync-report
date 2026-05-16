package com.helianhealth.family.he.health.api.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author bellliu .
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveReportItemDto implements Serializable {

    /**
     * 检查项id
     */
    private String itemId;

    /**
     * 检查项名称
     */
    private String itemName;

    /**
     * 科室小结
     */
    private String briefSummary;

    /**
     * 检查医生
     */
    private String checker;

    /**
     * 指标列表
     */
    private List<ResolveReportNodeDto> reportNodeList;

}
