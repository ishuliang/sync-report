package com.helianhealth.family.he.admin.api.report.param;

import lombok.Data;

import java.util.List;

/**
 * @author tx
 * @date 2022/11/4 13:29
 */
@Data
public class ResolveReportItemParam {

    /**
     * 检查项id
     */
    private String itemId;

    /**
     * 检查项名称
     */
    private String itemName;

    /**
     * 项目结论（科室小结）
     */
    private String briefSummary;

    /**
     * 项目列表
     */
    private List<ResolveReportNodeParam> reportNodeList;

}
