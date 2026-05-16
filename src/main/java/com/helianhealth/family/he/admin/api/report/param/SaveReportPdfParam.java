package com.helianhealth.family.he.admin.api.report.param;

import lombok.Data;

import java.util.Date;

/**
 * @author tx
 * @date 2022/11/3 16:20
 */
@Data
public class SaveReportPdfParam {

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
    private Date checkTime;

    /**
     * 报告oss链接
     */
    private String url;

}
