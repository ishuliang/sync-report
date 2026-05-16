package com.helianhealth.family.he.admin.model.report.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tx
 * @date 2022/11/4 10:55
 */
@Data
public class ManualFillReportParam implements Serializable {

    /**
     * 档案id
     */
    private Integer memberId;

    /**
     * 体检医院
     */
    private String stationName;

    /**
     * 体检时间
     */
    private String checkTime;

    /**
     * 工作单位
     */
    private String companyName;

    /**
     * 体检批次
     */
    private String batchName;

    /**
     * 总检结论
     */
    private List<ManualResolveReportDto.Conclusion> conclusions;

    /**
     * 检查项类表
     */
    private List<ResolveReportItemParam> reportItemList;

}
