package com.helianhealth.family.he.health.api.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author bellliu
 * @date 2022/6/17
 */
@Data
public class ReportPositiveItemDto implements Serializable {
    /**
     * 项目Id
     */
    private String itemId;
    /**
     * 项目名称
     */
    private String itemName;
    /**
     * 阳性等级
     */
    private Integer itemLevel;
    /**
     * 科室小结
     */
    private String briefSummary;
    /**
     * 科室建议
     */
    private String itemSuggest;
}
